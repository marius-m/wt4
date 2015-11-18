package lt.markmerkk.controllers;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lt.markmerkk.storage.entities.Log;
import lt.markmerkk.storage.entities.Project;
import lt.markmerkk.storage.entities.Task;
import lt.markmerkk.storage.entities.annotations.TableIndex;
import lt.markmerkk.storage.entities.table.LogTable;
import lt.markmerkk.utils.HourGlass;
import lt.markmerkk.utils.LogDisplayController;
import lt.markmerkk.utils.Logger;
import lt.markmerkk.utils.TableDisplayController;
import lt.markmerkk.utils.TaskController;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tmatesoft.sqljet.core.table.SqlJetScope;

/**
 * Created by mariusmerkevicius on 11/16/15.
 */
public class MainController extends BaseController {
  private final TaskController taskController;
  private final Logger logger;
  private final HourGlass hourGlass;
  private final DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");
  private final DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  private ObservableList<LogTable> logs;
  private ObservableList<Task> tasks;
  private ObservableList<Project> projects;
  private Project projectFilter = null;
  private DateTime filterDate;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextField outputDuration;
  @FXML TextField inputTask;
  @FXML TextArea inputComment;
  @FXML Button buttonClock;
  @FXML Button buttonEnter;

  @FXML TableView tableLogs;

  @FXML Text totalView;
  @FXML BorderPane footer;
  DatePicker datePicker;

  public MainController() {
    taskController = new TaskController(taskControllerListener);
    logger = new Logger();
    logger.setListener(loggerListener);
    hourGlass = new HourGlass();
    hourGlass.setListener(hourglassListener);
  }

  @Override
  public void setupController(BaseControllerDelegate listener, Scene scene, Stage primaryStage) {
    super.setupController(listener, scene, primaryStage);
    scene.getStylesheets().add(getClass().getResource("/text-field-red-border.css").toExternalForm());

    initViewListeners();
    initViews();

    notifyProjectsChanged();
    notifyTasksChanged();
    notifyLogsChanged();
  }

  //region Init

  /**
   * Initializes missing views for the controller
   */
  private void initViews() {
    // Initialize the DatePicker for birthday
    datePicker = new DatePicker(Locale.ENGLISH);
    datePicker.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    datePicker.getCalendarView().todayButtonTextProperty().set("Today");
    datePicker.getCalendarView().setShowWeeks(false);
    datePicker.getStylesheets().add("datepicker.css");
    datePicker.selectedDateProperty().addListener(new ChangeListener<Date>() {
      @Override
      public void changed(ObservableValue<? extends Date> observableValue, Date date, Date date2) {
        filterDate = new DateTime(date2);
        notifyLogsChanged();
      }
    });
    DateTime today = new DateTime().withTime(0,0,0,0);
    datePicker.setSelectedDate(today.toDate());
    footer.setRight(datePicker);

    LogDisplayController logDisplayController = new LogDisplayController(tableLogs, logs, listener);
  }

  /**
   * Initializes listeners for the views
   */
  private void initViewListeners() {
    inputFrom.textProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        hourGlass.updateTimers(filterDate, inputFrom.getText(), inputTo.getText());
      }
    });

    inputTo.textProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        hourGlass.updateTimers(filterDate, inputFrom.getText(), inputTo.getText());
      }
    });

    // Comment event listeners
    inputComment.setText("");
    inputComment.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent t) {
        if (t.getCode() == KeyCode.ENTER) {
          logger.log(inputComment.getText());
        }
      }
    });

    // Timer configuration
    buttonClock.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
        else hourGlass.stop();
      }
    });

    buttonEnter.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        logger.log(inputComment.getText());
      }
    });

  }

  //endregion


  //region Convenience

  /**
   * Adds an indicator as an error for the text field
   * @param tf provided text field
   */
  private void reportError(TextField tf) {
    ObservableList<String> styleClass = tf.getStyleClass();
    if (!styleClass.contains("error"))
      styleClass.add("error");
  }

  /**
   * Removes error indicator for the text field
   * @param tf provided text field
   */
  private void clearError(TextField tf) {
    ObservableList<String> styleClass = tf.getStyleClass();
    styleClass.removeAll(Collections.singleton("error"));
  }

  /**
   * Updates tasks from the database
   */
  private void notifyTasksChanged() {
    ArrayList<Task> tasks = taskStorage.readAll();
    if (this.tasks == null)
      this.tasks = FXCollections.observableArrayList();
    this.tasks.clear();
    if (projectFilter != null) {
      for (Task task : tasks)
        if (task.getTitle().startsWith(projectFilter.getTitle()))
          this.tasks.add(task);
    } else
      this.tasks.addAll(tasks);
  }

  /**
   * Updates projects from the database
   */
  private void notifyProjectsChanged() {
    ArrayList<Project> projects = projectStorage.readAll();
    if (this.projects == null)
      this.projects = FXCollections.observableArrayList();
    this.projects.clear();
    //this.projects.add(masterProject);
    this.projects.addAll(projects);
    //if (projectsBox != null)
    //  projectsBox.setValue(masterProject);
  }

  /**
   * Updates logs from the database
   */
  private void notifyLogsChanged() {
    ArrayList<Log> logs = logStorage.readWithScope(Log.class.getAnnotation(TableIndex.class).name(),
        new SqlJetScope(
            new Object[]{filterDate.getMillis()},
            new Object[]{filterDate.plusDays(1).getMillis()}
        )
    );
    if (this.logs == null)
      this.logs = FXCollections.observableArrayList();
    this.logs.clear();
    for (Log log : logs)
      this.logs.add(log.toTableEntity());
    countTotal();
  }

  /**
   * Counts total time spent for the day
   */
  private void countTotal() {
    long total = 0;
    for (LogTable log : logs) {
      total += log.getDuration();
    }
    totalView.setText(Log.formatDuration(total));
  }

  //endregion

  //region Listeners

  TableDisplayController.Listener<LogTable> listener =
      new TableDisplayController.Listener<LogTable>() {
        @Override public void onUpdate(LogTable object) {
          mMasterListener.pushScene("/update_log.fxml", object);
        }

        @Override public void onDelete(LogTable object) {
          logStorage.delete(object.getId());
          notifyLogsChanged();
        }
      };

  private TaskController.ResourceListener taskControllerListener = new TaskController.ResourceListener() {
    @Override
    public ObservableList<Task> getTasks() {
      return tasks;
    }

    @Override
    public ObservableList<Project> getProjects() {
      return projects;
    }

    @Override
    public Long onNewProject(Project newProject) {
      return (Long)projectStorage.insert(newProject);
    }

    @Override
    public Long onNewTask(Task newTask) {
      return (Long)taskStorage.insert(newTask);
    }

    @Override
    public Long onUpdateTask(Task updateTask) {
      return (Long)taskStorage.update(updateTask);
    }

    @Override
    public void onDataChange() {
      notifyTasksChanged();
      notifyProjectsChanged();
    }
  };

  private HourGlass.Listener hourglassListener = new HourGlass.Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.setText(shortFormat.print(start));
      inputTo.setText(shortFormat.print(end));
      outputDuration.setText(Log.formatDuration(duration));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.setText("");
      inputTo.setText("");
      outputDuration.setText("");
    }

    @Override
    public void onTick(final long start, final long end, final long duration) {
      clearError(inputFrom);
      clearError(inputTo);
      clearError(outputDuration);
      outputDuration.setText(Log.formatDuration(duration));
    }

    @Override public void onError(HourGlass.Error error) {
      switch (error) {
        case START:
        case END:
          reportError(inputFrom);
          reportError(inputTo);
          reportError(outputDuration);
          break;
        case DURATION:
          reportError(outputDuration);
          break;
      }
      outputDuration.setText(error.getMessage());
    }

  };

  private Logger.Listener loggerListener = new Logger.Listener() {
    @Override
    public void onParse(DateTime startTime, DateTime endTime, String comment, String task) {
      try {
        Log.Builder logBuilder = new Log.Builder();
        // Get days from
        if (startTime == null && hourGlass.getStartMillis() != 0)
          startTime = new DateTime(hourGlass.getStartMillis());
        if (startTime == null)
          startTime = new DateTime(DateTimeUtils.currentTimeMillis());
        int days = Days.daysBetween(filterDate.toLocalDate(), startTime.toLocalDate()).getDays();
        startTime = startTime.minusDays(days);
        logBuilder.setStart(startTime.getMillis());
        if (endTime == null)
          endTime = new DateTime(DateTimeUtils.currentTimeMillis());
        endTime = endTime.minusDays(days);
        logBuilder.setEnd(endTime.getMillis());
        taskController.handle(task);
        String taskName = TaskController.inspectAndFormTitle(task);
        if (taskName != null)
          logBuilder.setCategory(taskName);
        String projectName = TaskController.splitName(taskName);
        Project project = Project.getProjectWithTitle(projects, projectName);
        if (project != null) {
          //ArrayList<String> logs = GitController2.getStatus(
          //    project.getPaths(),
          //    startTime,
          //    endTime);
          //logBuilder.setGitMessage(logs);
        }

        logBuilder.setMessage(comment);
        Log log = logBuilder.build();
        logStorage.insert(log);

        // Resetting controls
        inputComment.setText("");
        hourGlass.restart();
        notifyLogsChanged();
        notifyTasksChanged();
        notifyProjectsChanged();
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
      }
    }
  };

  //endregion

  //region World events

  @Override
  public void resume() {
    super.resume();
    notifyProjectsChanged();
    notifyLogsChanged();
  }


  @Override public void destroy() {
    super.destroy();
    hourGlass.stop();
  }

  //endregion

}

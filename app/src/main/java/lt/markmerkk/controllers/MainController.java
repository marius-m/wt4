package lt.markmerkk.controllers;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lt.markmerkk.jira.WorkExecutor;
import lt.markmerkk.jira.WorkReporter;
import lt.markmerkk.jira.WorkScheduler2;
import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.WorkerLoadingListener;
import lt.markmerkk.jira.interfaces.WorkerOutputListener;
import lt.markmerkk.jira.workers.JiraWorkerLogin;
import lt.markmerkk.jira.workers.JiraWorkerOpenIssues;
import lt.markmerkk.jira.workers.JiraWorkerPullMerge;
import lt.markmerkk.jira.workers.JiraWorkerPushNew;
import lt.markmerkk.jira.workers.JiraWorkerTodayIssues;
import lt.markmerkk.jira.workers.JiraWorkerWorklogForIssue;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.jobs.DeleteJob;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryListJob;
import lt.markmerkk.utils.LogDisplayController;
import lt.markmerkk.utils.TableDisplayController;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import lt.markmerkk.utils.os_formatter.IOSOutput;
import lt.markmerkk.utils.os_formatter.OSXOutput;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusmerkevicius on 11/16/15.
 * Represents the main controller to display UI elements and business logic
 */
public class MainController extends BaseController {
  private final HourGlass hourGlass;
  private final DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");
  private final DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  private ObservableList<SimpleLog> logs;
  private ObservableList<SimpleIssue> issues;
  private DateTime filterDate;

  @FXML TabPane tabPane;

  @FXML Tab tabWork;
  @FXML Tab tabJira;
  @FXML Tab tabIssues;
  @FXML Tab tabCreate;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextField outputDuration;
  @FXML TextArea inputComment;
  @FXML Button buttonClock;
  @FXML Button buttonEnter;
  @FXML Button buttonOpen;
  @FXML Button buttonNew;
  @FXML TextArea outputLogger;

  @FXML TextField inputHost;
  @FXML TextField inputUsername;
  @FXML PasswordField inputPassword;
  @FXML Button buttonTest;

  @FXML TableView tableLogs;

  @FXML Text totalView;
  @FXML BorderPane footer;
  @FXML ProgressIndicator progressIndicator;
  DatePicker datePicker;

  @FXML ComboBox<SimpleIssue> inputTaskCombo;

  WorkExecutor asyncWorkExecutor;
  IOSOutput osOutput;

  public MainController() {
    hourGlass = new HourGlass();
    hourGlass.setListener(hourglassListener);
    osOutput = new OSXOutput();
  }

  @Override
  public void setupController(BaseControllerDelegate listener, Scene scene, Stage primaryStage) {
    super.setupController(listener, scene, primaryStage);
    scene.getStylesheets().add(
        getClass().getResource("/text-field-red-border.css").toExternalForm());

    asyncWorkExecutor = new WorkExecutor(new WorkReporter(), workerOutputListener);
    asyncWorkExecutor.setLoadingListener(workerLoading);

    initViewListeners();
    initViews();

    Platform.runLater(() -> {
      progressIndicator.setManaged(false);
      progressIndicator.setVisible(false);
    });
  }

  @Override void onInternalOutput(String message) {
    outputLogger.appendText(message);
  }

  //region Init

  /**
   * Initializes missing views for the controller
   */
  private void initViews() {
    inputTaskCombo.setOnKeyReleased(new EventHandler<KeyEvent>() {
      @Override public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT ||
            event.getCode() == KeyCode.LEFT ||
            event.getCode() == KeyCode.UP ||
            event.getCode() == KeyCode.DOWN ||
            event.getCode() == KeyCode.HOME ||
            event.getCode() == KeyCode.END ||
            event.getCode() == KeyCode.TAB) return;
        notifyIssuesChanged();
        inputTaskCombo.show();
      }
    });
    datePicker = new DatePicker(Locale.ENGLISH);
    datePicker.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    datePicker.getCalendarView().todayButtonTextProperty().set("Today");
    datePicker.getCalendarView().setShowWeeks(false);
    datePicker.getStylesheets().add("datepicker.css");
    datePicker.selectedDateProperty().addListener(new ChangeListener<Date>() {
      @Override
      public void changed(ObservableValue<? extends Date> observableValue, Date date, Date date2) {
        filterDate = new DateTime(date2);
        hourGlass.setCurrentDay(filterDate);
        notifyLogsChanged();
      }
    });
    DateTime today = new DateTime().withTime(0,0,0,0);
    datePicker.setSelectedDate(today.toDate());
    footer.setRight(datePicker);

    LogDisplayController logDisplayController = new LogDisplayController(tableLogs, logs, listener);
    updateUI();
  }

  /**
   * Initializes listeners for the views
   */
  private void initViewListeners() {

    buttonOpen.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        SimpleIssue selectedIssue =
            issues.get(inputTaskCombo.getSelectionModel().getSelectedIndex());
        if (selectedIssue == null) return;
        URI issuePath = null;
        try {
          //issuePath = new URI(inputHost.getText() + "/secure/CreateIssue!default.jspa");
          issuePath = new URI(inputHost.getText()+"/browse/"+selectedIssue.getKey());
          final Tab newTab = new Tab(selectedIssue.getKey());
          newTab.setClosable(true);
          WebView taskView = new WebView();
          newTab.setContent(taskView);
          tabPane.getTabs().add(newTab);
          tabPane.getSelectionModel().select(newTab);
          taskView.getEngine().load(issuePath.toString());
        } catch (URISyntaxException e) {
          e.printStackTrace();
        }
      }
    });

    buttonNew.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        URI issuePath = null;
        try {
          issuePath = new URI(inputHost.getText() + "/secure/CreateIssue!default.jspa");
          final Tab newTab = new Tab("New");
          newTab.setClosable(true);
          WebView taskView = new WebView();
          newTab.setContent(taskView);
          tabPane.getTabs().add(newTab);
          tabPane.getSelectionModel().select(newTab);
          taskView.getEngine().load(issuePath.toString());
        } catch (URISyntaxException e) {
          e.printStackTrace();
        }
      }
    });

    //tabCreate.setOnSelectionChanged(new EventHandler<Event>() {
    //  @Override public void handle(Event event) {
    //  }
    //});

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
          logWork();
        }
      }
    });

    // Timer configuration
    buttonClock.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
        else hourGlass.stop();
        updateUI();
      }
    });

    buttonTest.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent event) {
        if (asyncWorkExecutor.isLoading() || asyncWorkExecutor.hasMore()) {
          asyncWorkExecutor.cancel();
          return;
        }
        try {
          Credentials credentials =
              new Credentials(inputUsername.getText(), inputPassword.getText(),
                  inputHost.getText());
          asyncWorkExecutor.executeScheduler(
              new WorkScheduler2(credentials,
                  new JiraWorkerLogin(),
                  new JiraWorkerPushNew(executor, filterDate),
                  new JiraWorkerTodayIssues(filterDate),
                  new JiraWorkerWorklogForIssue(inputUsername.getText(), filterDate),
                  new JiraWorkerPullMerge(executor),
                  new JiraWorkerOpenIssues(executor)
              )
          );
        } catch (Exception e) {
          log.info("Error: "+e.getMessage());
        }
      }
    });

    buttonEnter.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        logWork();
      }
    });

  }

  private void updateUI() {
    boolean disableElement = (hourGlass.getState() == HourGlass.State.STOPPED);
    inputFrom.setDisable(disableElement);
    inputTo.setDisable(disableElement);
    inputTaskCombo.setDisable(disableElement);
    inputComment.setDisable(disableElement);
    outputDuration.setDisable(disableElement);
    buttonEnter.setDisable(disableElement);
    buttonOpen.setDisable(disableElement);
    buttonNew.setDisable(disableElement);
  }

  //endregion

  //region Convenience

  /**
   * Gathers data and logs work to a database
   */
  private void logWork() {
    try {
      if (hourGlass.getState() == HourGlass.State.STOPPED)
        throw new IllegalArgumentException("Please run timer first!");
      if (!hourGlass.isValid())
        throw new IllegalArgumentException("Error calculating time!");
      SimpleLog log = new SimpleLogBuilder(DateTime.now().getMillis())
          .setStart(hourGlass.reportStart().getMillis())
          .setEnd(hourGlass.reportEnd().getMillis())
          .setTask(inputTaskCombo.getEditor().getText())
          .setComment(inputComment.getText()).build();
      executor.execute(new InsertJob(SimpleLog.class, log));
      this.log.info("Saving: "+log.toString());

      // Resetting controls
      inputComment.setText("");
      hourGlass.restart();
      notifyLogsChanged();
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

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
   * Updates logs from the database
   */
  private void notifyLogsChanged() {
    QueryListJob<SimpleLog> queryJob = new QueryListJob<>(SimpleLog.class,
        () -> "(start > " + filterDate.getMillis()
            + " AND "
            + "end < " + filterDate.plusDays(1).getMillis() + ") ORDER BY start ASC");
    executor.execute(queryJob);
    if (logs == null)
      logs = FXCollections.observableArrayList();
    logs.clear();
    if (queryJob.result() != null)
      logs.addAll(queryJob.result());
    countTotal();
  }

  /**
   * Updates issues from the database
   */
  private void notifyIssuesChanged() {
    QueryListJob<SimpleIssue> queryJob = new QueryListJob<>(SimpleIssue.class);
    if (!Utils.isEmpty(inputTaskCombo.getEditor().getText()) && inputTaskCombo.getEditor().getText().length() >= 2)
      queryJob = new QueryListJob<>(SimpleIssue.class, () -> "("
          + "key like '%"+inputTaskCombo.getEditor().getText()+"%' "
          + "OR "
          + "description like '%"+inputTaskCombo.getEditor().getText()+"%' "
          + ")");
    executor.execute(queryJob);
    if (issues == null)
      issues = FXCollections.observableArrayList();
    issues.clear();
    if (queryJob.result() != null)
      issues.addAll(queryJob.result());
    inputTaskCombo.setItems(issues);
  }

  /**
   * Counts total time spent for the day
   */
  private void countTotal() {
    long total = 0;
    for (SimpleLog log : logs)
      total += log.getDuration();
    totalView.setText(Utils.formatShortDuration(total));
  }

  //endregion

  //region Listeners

  WorkerOutputListener workerOutputListener = new WorkerOutputListener() {
    @Override public void onOutput(String message) {
      log.info("Jira: " + message);
    }
  };

  WorkerLoadingListener workerLoading = new WorkerLoadingListener() {
    @Override
    public void onLoadChange(boolean loading) {
      progressIndicator.setManaged(loading);
      progressIndicator.setVisible(loading);
      inputUsername.setDisable(loading);
      inputPassword.setDisable(loading);
      inputHost.setDisable(loading);
      buttonTest.setText((loading) ? "Cancel" : "Refresh");
      if (!loading) {
        notifyLogsChanged();
        notifyIssuesChanged();
      }
    }

    @Override
    public void onSyncChange(boolean syncing) {

    }
  };


  TableDisplayController.Listener<SimpleLog> listener =
      new TableDisplayController.Listener<SimpleLog>() {
        @Override public void onUpdate(SimpleLog object) {
          MainController.this.log.info("Updating log: "+object.toString());
          masterListener.pushScene("/update_log.fxml", object);
        }

        @Override public void onDelete(SimpleLog object) {
          MainController.this.log.info("Deleting log: "+object.toString());
          executor.execute(new DeleteJob(SimpleLog.class, object));
          notifyLogsChanged();
        }

        @Override public void onClone(SimpleLog object) {
          SimpleLog newLog = new SimpleLogBuilder()
              .setStart(object.getStart())
              .setEnd(object.getEnd())
              .setTask(object.getTask())
              .setComment(object.getComment())
              .build();
          executor.execute(new InsertJob(SimpleLog.class, newLog));
          notifyLogsChanged();
        }
      };

  private HourGlass.Listener hourglassListener = new HourGlass.Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.setText(shortFormat.print(start));
      inputTo.setText(shortFormat.print(end));
      outputDuration.setText(Utils.formatShortDuration(duration));
      MainController.this.log.info(
          "Starting: " + shortFormat.print(start) + " / " + shortFormat.print(end));
      osOutput.onDurationMessage(Utils.formatShortDuration(duration));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.setText("");
      inputTo.setText("");
      outputDuration.setText("");
      MainController.this.log.info(
          "Stopping: " + shortFormat.print(start) + " / " + shortFormat.print(end));
      osOutput.onDurationMessage("");
    }

    @Override
    public void onTick(final long start, final long end, final long duration) {
      clearError(inputFrom);
      clearError(inputTo);
      clearError(outputDuration);
      String newFrom = shortFormat.print(start);
      if (!newFrom.equals(inputFrom.getText()) && !inputFrom.isFocused()) {
        inputFrom.setText(newFrom);
        osOutput.onDurationMessage(Utils.formatShortDuration(duration));
      }
      String newTo = shortFormat.print(end);
      if (!newTo.equals(inputTo.getText()) && !inputTo.isFocused()) {
        inputTo.setText(newTo);
        osOutput.onDurationMessage(Utils.formatShortDuration(duration));
      }
      outputDuration.setText(Utils.formatShortDuration(duration));
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

  //endregion

  //region World events

  @Override public void create(Object data) {
    super.create(data);
    asyncWorkExecutor.onStart();
  }

  @Override
  public void resume() {
    super.resume();
    notifyLogsChanged();
    notifyIssuesChanged();

    inputHost.setText(settings.getHost());
    inputUsername.setText(settings.getName());
  }

  @Override public void pause() {
    super.pause();
    settings.setHost(inputHost.getText());
    settings.setName(inputUsername.getText());
  }

  @Override public void destroy() {
    super.destroy();
    hourGlass.stop();
    asyncWorkExecutor.onStop();
  }

  //endregion

}

package lt.markmerkk.ui.clock;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.ui.clock.utils.SimpleDatePickerConverter;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import lt.markmerkk.utils.hourglass.interfaces.Listener;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter for the clock for logging info.
 */
public class ClockPresenter implements Initializable {

  @Inject DBProdExecutor dbExecutor;
  @Inject HourGlass hourGlass;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextField outputDuration;
  //@FXML TextField inputTask;
  @FXML TextArea inputComment;
  @FXML Button buttonClock;
  @FXML Button buttonEnter;
  @FXML Button buttonOpen;
  @FXML Button buttonNew;
  //@FXML TextArea outputLogger;
  @FXML ComboBox<SimpleIssue> inputTaskCombo;
  @FXML DatePicker inputTargetDate;

  DateTime targetDate;

  @Override public void initialize(URL location, ResourceBundle resources) {
    hourGlass.setListener(hourglassListener);
    inputFrom.textProperty().addListener(timeChangeListener);
    inputTo.textProperty().addListener(timeChangeListener);
    inputComment.setOnKeyReleased(onKeyboardEnterEventHandler);
    buttonClock.setOnMouseClicked(onMouseClockEventHandler);
    buttonEnter.setOnMouseClicked(onMouseEnterEventHandler);
    inputTargetDate.setConverter(new SimpleDatePickerConverter());
    inputTargetDate.editorProperty().addListener(targetDateListener);
    String dateNow = SimpleLog.longDateFormat.print(DateTime.now());
    inputTargetDate.getEditor().setText(dateNow);
    updateUI();
  }

  //region Convenience

  /**
   * Updates UI depending on {@link HourGlass} state
   */
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
    inputTargetDate.setDisable(disableElement);
  }

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
      dbExecutor.execute(new InsertJob(SimpleLog.class, log));

      // Resetting controls
      inputComment.setText("");
      hourGlass.restart();
      //notifyLogsChanged();
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  //endregion

  //region Listeners

  EventHandler<MouseEvent> onMouseEnterEventHandler = new EventHandler<MouseEvent>() {
    @Override public void handle(MouseEvent mouseEvent) {
      logWork();
    }
  };

  EventHandler<MouseEvent> onMouseClockEventHandler = new EventHandler<MouseEvent>() {
    @Override public void handle(MouseEvent mouseEvent) {
      if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
      else hourGlass.stop();
      updateUI();
    }
  };

  ChangeListener<String> timeChangeListener = new ChangeListener<String>() {
    @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
        String newValue) {
      hourGlass.updateTimers(targetDate, inputFrom.getText(), inputTo.getText());
    }
  };

  EventHandler<KeyEvent> onKeyboardEnterEventHandler = new EventHandler<KeyEvent>() {
    public void handle(KeyEvent t) {
      if (t.getCode() == KeyCode.ENTER) {
        logWork();
      }
    }
  };

  ChangeListener<TextField> targetDateListener = new ChangeListener<TextField>() {
    @Override
    public void changed(ObservableValue<? extends TextField> observable, TextField oldValue,
        TextField newValue) {
      targetDate = SimpleLog.longDateFormat.parseDateTime(newValue.getText());
      hourGlass.setCurrentDay(targetDate);
      //notifyLogsChanged();
    }
  };

  private Listener hourglassListener = new Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.setText(SimpleLog.shortFormat.print(start));
      inputTo.setText(SimpleLog.shortFormat.print(end));
      outputDuration.setText(Utils.formatShortDuration(duration));
      //MainController.this.log.info(
      //    "Starting: " + shortFormat.print(start) + " / " + shortFormat.print(end));
      //osOutput.onDurationMessage(Utils.formatShortDuration(duration));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.setText("");
      inputTo.setText("");
      outputDuration.setText("");
      //MainController.this.log.info(
      //    "Stopping: " + shortFormat.print(start) + " / " + shortFormat.print(end));
      //osOutput.onDurationMessage("");
    }

    @Override
    public void onTick(final long start, final long end, final long duration) {
      clearError(inputFrom);
      clearError(inputTo);
      clearError(outputDuration);
      String newFrom = SimpleLog.shortFormat.print(start);
      if (!newFrom.equals(inputFrom.getText()) && !inputFrom.isFocused()) {
        inputFrom.setText(newFrom);
        //osOutput.onDurationMessage(Utils.formatShortDuration(duration));
      }
      String newTo = SimpleLog.shortFormat.print(end);
      if (!newTo.equals(inputTo.getText()) && !inputTo.isFocused()) {
        inputTo.setText(newTo);
        //osOutput.onDurationMessage(Utils.formatShortDuration(duration));
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

    @Override public void onSuggestTime(DateTime start, DateTime end) {
      inputFrom.setText(SimpleLog.shortFormat.print(start));
      inputTo.setText(SimpleLog.shortFormat.print(end));
    }
  };

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

  //endregion

}

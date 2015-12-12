package lt.markmerkk.ui.clock;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

  public static final String BUTTON_LABEL_ENTER = "Enter";

  @Inject DBProdExecutor dbExecutor;
  @Inject HourGlass hourGlass;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextArea inputComment;
  //@FXML Button buttonClock;
  @FXML ToggleButton buttonClock;
  @FXML Button buttonEnter;
  @FXML Button buttonOpen;
  @FXML Button buttonNew;
  @FXML ComboBox<SimpleIssue> inputTaskCombo;

  @Override public void initialize(URL location, ResourceBundle resources) {
    hourGlass.setCurrentDay(DateTime.now());
    hourGlass.setListener(hourglassListener);
    inputFrom.textProperty().addListener(timeChangeListener);
    inputTo.textProperty().addListener(timeChangeListener);
    updateUI();
  }

  public void onClockClick() {
    if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
    else hourGlass.stop();
    buttonClock.setSelected(hourGlass.getState() == HourGlass.State.RUNNING);
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
    buttonEnter.setDisable(disableElement);
    buttonOpen.setDisable(disableElement);
    buttonNew.setDisable(disableElement);
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

  ChangeListener<String> timeChangeListener = new ChangeListener<String>() {
    @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
        String newValue) {
      hourGlass.updateTimers(inputFrom.getText(), inputTo.getText());
    }
  };

  private Listener hourglassListener = new Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.setText(HourGlass.longFormat.print(start));
      inputTo.setText(HourGlass.longFormat.print(end));
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, Utils.formatShortDuration(
          duration)));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.setText("");
      inputTo.setText("");
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, Utils.formatShortDuration(duration)));
    }

    @Override
    public void onTick(final long start, final long end, final long duration) {
      clearError(inputFrom);
      clearError(inputTo);
      String newFrom = HourGlass.longFormat.print(start);
      if (!newFrom.equals(inputFrom.getText()) && !inputFrom.isFocused()) {
        inputFrom.setText(newFrom);
      }
      String newTo = HourGlass.longFormat.print(end);
      if (!newTo.equals(inputTo.getText()) && !inputTo.isFocused()) {
        inputTo.setText(newTo);
      }
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER,
          Utils.formatShortDuration(duration)));
    }

    @Override public void onError(HourGlass.Error error) {
      switch (error) {
        case START:
        case END:
          reportError(inputFrom);
          reportError(inputTo);
          break;
        case DURATION:
          break;
      }
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, error.getMessage()));
    }

    @Override public void onSuggestTime(DateTime start, DateTime end) {
      inputFrom.setText(HourGlass.longFormat.print(start));
      inputTo.setText(HourGlass.longFormat.print(end));
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

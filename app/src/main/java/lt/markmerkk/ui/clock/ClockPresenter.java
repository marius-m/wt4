package lt.markmerkk.ui.clock;

import java.net.URL;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.inject.Inject;
import lt.markmerkk.storage2.BasicIssueStorage;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter for the clock for logging info.
 */
public class ClockPresenter implements Initializable {

  public static final String BUTTON_LABEL_ENTER = "Enter";

  @Inject HourGlass hourGlass;
  @Inject BasicLogStorage logStorage;
  @Inject BasicIssueStorage issueStorage;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextArea inputComment;
  @FXML ToggleButton buttonClock;
  @FXML Button buttonEnter;
  @FXML Button buttonOpen;
  @FXML Button buttonNew;
  @FXML ComboBox<SimpleIssue> inputTaskCombo;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    inputTaskCombo.setItems(issueStorage.getData());
    inputTaskCombo.setOnKeyReleased(comboKeyListener);
    hourGlass.setCurrentDay(DateTime.now());
    hourGlass.setListener(hourglassListener);
    inputFrom.textProperty().addListener(timeChangeListener);
    inputTo.textProperty().addListener(timeChangeListener);
    updateUI();
  }

  public void onClickClock() {
    if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
    else hourGlass.stop();
    buttonClock.setSelected(hourGlass.getState() == HourGlass.State.RUNNING);
    updateUI();
  }

  public void onClickEnter() {
    logWork();
  }

  public void onClickNew() {
  }

  public void onClickForward() {

  }

  public void onClickSettings() {
    listener.onSettings();
  }

  //region Convenience

  /**
   * Updates UI depending on {@link HourGlass} state
   */
  private void updateUI() {
    boolean disableElement = (hourGlass.getState() == HourGlass.State.STOPPED);
    inputFrom.setEditable(!disableElement);
    inputTo.setEditable(!disableElement);
    inputComment.setEditable(!disableElement);
    inputComment.setPromptText( (disableElement) ? "Start timer to log work!" : "Go go go!");
    buttonEnter.setDisable(disableElement);
    //inputTaskCombo.setEditable(!disableElement);
    //buttonOpen.setDisable(disableElement);
    //buttonNew.setDisable(disableElement);
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
      logStorage.insert(log);

      // Resetting controls
      inputComment.setText("");
      hourGlass.restart();
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  //endregion

  //region Listeners

  EventHandler<KeyEvent> comboKeyListener = new EventHandler<KeyEvent>() {
    @Override public void handle(KeyEvent event) {
      if (event.getCode() == KeyCode.ESCAPE ||
          event.getCode() == KeyCode.ENTER) {
        issueStorage.updateFilter(inputTaskCombo.getEditor().getText());
        inputTaskCombo.hide();
        return;
      }
      if (event.getCode() == KeyCode.UP ||
          event.getCode() == KeyCode.DOWN) {
        inputTaskCombo.show();
        return;
      }

      if (event.getCode() == KeyCode.RIGHT ||
          event.getCode() == KeyCode.LEFT ||
          event.getCode() == KeyCode.HOME ||
          event.getCode() == KeyCode.END ||
          event.getCode() == KeyCode.DELETE ||
          event.getCode() == KeyCode.TAB)
        return;
      issueStorage.updateFilter(inputTaskCombo.getEditor().getText());
      inputTaskCombo.show();
    }
  };

  ChangeListener<String> timeChangeListener = new ChangeListener<String>() {
    @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
        String newValue) {
      hourGlass.updateTimers(inputFrom.getText(), inputTo.getText());
      logStorage.setTargetDate(inputFrom.getText());
    }
  };

  private HourGlass.Listener hourglassListener = new HourGlass.Listener() {
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

  /**
   * Helper listener for the clock window
   */
  public interface Listener {
    /**
     * Occurs settings window is requested
     */
    void onSettings();

    /**
     * Occurs when url should be opened
     */
    void onOpen(String url);

  }

}

package lt.markmerkk.ui.clock;

import java.net.URI;
import java.net.URISyntaxException;
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
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.inject.Inject;
import lt.markmerkk.storage2.BasicIssueStorage;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.utils.UserSettings;
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
  @Inject UserSettings settings;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextArea inputComment;
  @FXML ToggleButton buttonClock;
  @FXML Button buttonEnter;
  @FXML Button buttonOpen;
  @FXML Button buttonNew;
  @FXML Button buttonSettings;
  @FXML ComboBox<SimpleIssue> inputTaskCombo;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    inputTaskCombo.setItems(issueStorage.getData());
    inputTaskCombo.setOnKeyReleased(comboKeyListener);
    inputFrom.setTooltip(new Tooltip("Worklog start" +
        "\n\nStart time for the current log. " +
        "It can be edited whenever timer is running. " +
        "\nThis timer acts as today's date, " +
        "changing this will change display for the whole work log."));
    inputTo.setTooltip(new Tooltip("Worklog end" +
        "\n\nEnd time for the current log." +
        "It can be edited whenever timer is running. "));
    inputTaskCombo.setTooltip(new Tooltip("Issue search bar " +
        "\n\nType in issue number, title to begin searching."));
    buttonClock.setTooltip(new Tooltip("Start/Stop " +
        "\n\nEnable/disable work timer."));
    buttonEnter.setTooltip(new Tooltip("Enter " +
        "\n\nEnters currently running work."));
    buttonOpen.setTooltip(new Tooltip("Forward " +
        "\n\nOpen selected issue details."));
    buttonNew.setTooltip(new Tooltip("New. " +
        "\n\nCreate new issue."));
    buttonSettings.setTooltip(new Tooltip("Settings. " +
        "\n\nSetting up remote host, user credentials."));
    inputComment.setTooltip(new Tooltip("Comment" +
        "\n\nEnter comment here for the work log."));
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
    try {
      URI newPath = new URI(settings.getHost()+"/secure/CreateIssue!default.jspa");
      listener.onOpen(newPath.toString(), "New task");
    } catch (URISyntaxException e) { }
  }

  public void onClickForward() {
    if (inputTaskCombo.getSelectionModel() == null) return;
    if (inputTaskCombo.getSelectionModel().getSelectedIndex() < 0) return;
    SimpleIssue selectedIssue =
        issueStorage.getData().get(inputTaskCombo.getSelectionModel().getSelectedIndex());
    if (selectedIssue == null) return;
    URI issuePath = null;
    try {
      issuePath = new URI(settings.getHost()+"/browse/"+selectedIssue.getKey());
      listener.onOpen(issuePath.toString(), selectedIssue.getKey());
    } catch (URISyntaxException e) { }
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
          .setStart(HourGlass.parseMillisFromText(inputFrom.getText()))
          .setEnd(HourGlass.parseMillisFromText(inputTo.getText()))
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
        inputTaskCombo.hide();
        return;
      }

      if (
          event.getCode() == KeyCode.UP ||
          event.getCode() == KeyCode.DOWN) {
        inputTaskCombo.show();
        return;
      }

      if (event.getCode() == KeyCode.RIGHT ||
          event.getCode() == KeyCode.LEFT ||
          event.getCode() == KeyCode.HOME ||
          event.getCode() == KeyCode.END ||
          event.getCode() == KeyCode.TAB)
        return;

      if (event.getCode() == KeyCode.BACK_SPACE ||
          event.getCode() == KeyCode.DELETE) {
        issueStorage.updateFilter(inputTaskCombo.getEditor().getText());
        inputTaskCombo.show();
        return;
      }

      if (Utils.isEmpty(event.getText())) return;

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
    void onOpen(String url, String title);

  }

}

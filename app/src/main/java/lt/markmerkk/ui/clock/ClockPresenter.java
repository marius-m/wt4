package lt.markmerkk.ui.clock;

import com.google.common.base.Strings;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javax.inject.Inject;
import lt.markmerkk.Main;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.utils.IssueSearchAdapter;
import lt.markmerkk.utils.SyncController;
import lt.markmerkk.utils.UserSettings;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import net.rcarz.jiraclient.Issue;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observables.JavaFxObservable;

/**
 * Created by mariusmerkevicius on 12/5/15. Represents the presenter for the clock for logging
 * info.
 */
public class ClockPresenter implements Initializable {
  public static final Logger logger = LoggerFactory.getLogger(ClockPresenter.class);
  public static final String BUTTON_LABEL_ENTER = "Enter";

  @Inject
  HourGlass hourGlass;
  @Inject
  BasicLogStorage logStorage;
  @Inject
  SyncController syncController;
  @Inject
  UserSettings settings;

  @FXML
  DatePicker inputTo;
  @FXML
  DatePicker inputFrom;
  @FXML
  TextArea inputComment;
  @FXML
  ToggleButton buttonClock;
  @FXML
  Button buttonEnter;
  @FXML
  Button buttonOpen;
  @FXML
  Button buttonSearch;
  //@FXML Button buttonNew;
  @FXML
  Button buttonSettings;

  @FXML ProgressIndicator taskLoadIndicator;
  @FXML ComboBox<Issue> inputTaskCombo;
  @FXML HBox topContainer;


  IssueSearchAdapter issueSearchAdapter;
  Listener listener;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    issueSearchAdapter = new IssueSearchAdapter(syncController, inputTaskCombo, taskLoadIndicator);
    JavaFxObservable.fromObservableValue(inputTaskCombo.getEditor().textProperty())
        .subscribe(newString -> {
          if (newString != null && newString.length() <= 2)
            inputTaskCombo.getSelectionModel().clearSelection();
          if (Strings.isNullOrEmpty(newString))
            inputTaskCombo.getSelectionModel().clearSelection();
          boolean visible = inputTaskCombo.getSelectionModel().getSelectedItem() != null;
          buttonOpen.setVisible(visible);
          buttonOpen.setManaged(visible);
        });

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
    buttonSettings.setTooltip(new Tooltip("Settings. " +
        "\n\nSetting up remote host, user credentials."));
    inputComment.setTooltip(new Tooltip("Comment" +
        "\n\nEnter comment here for the work log."));
    hourGlass.setCurrentDay(DateTime.now());
    hourGlass.setListener(hourglassListener);
    inputFrom.getEditor().textProperty().addListener(timeChangeListener);
    inputTo.getEditor().textProperty().addListener(timeChangeListener);
    inputFrom.setConverter(startDateConverter);
    inputTo.setConverter(endDateConverter);

    Platform.runLater(() -> {
      taskLoadIndicator.setManaged(false);
      taskLoadIndicator.setVisible(false);
      buttonOpen.setVisible(false);
      buttonOpen.setManaged(false);
    });
    updateUI();
  }

  //region Keyboard input

  public void onClickClock() {
    if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
    else hourGlass.stop();
    buttonClock.setSelected(hourGlass.getState() == HourGlass.State.RUNNING);
    updateUI();
  }

  public void onClickEnter() {
    logWork();
  }

  public void onClickOpen() {
    if (inputTaskCombo.getSelectionModel().getSelectedItem() == null) return;
    if (Strings.isNullOrEmpty(settings.getHost())) return;
    URI issuePath = null;
    try {
      issuePath = new URI(settings.getHost()+"/browse/"+inputTaskCombo.getSelectionModel().getSelectedItem().getKey());
      Main.hostServices.showDocument(issuePath.toString());
    } catch (URISyntaxException e) {
      logger.error("Cant open issue! ", e);
    }
  }

  public void onClickSearch() {
    issueSearchAdapter.doSearch();
  }

  public void onClickSettings() {
    listener.onSettings();
  }

  //endregion

  //region Convenience

  /**
   * Updates UI depending on {@link HourGlass} state
   */
  private void updateUI() {
    boolean disableElement = (hourGlass.getState() == HourGlass.State.STOPPED);
    inputFrom.setEditable(!disableElement);
    inputFrom.setDisable(disableElement);
    inputTo.setEditable(!disableElement);
    inputTo.setDisable(disableElement);
    inputComment.setEditable(!disableElement);
    inputComment.setPromptText((disableElement) ? "Start timer to log work!" : "Go go go!");
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
          .setStart(HourGlass.parseMillisFromText(inputFrom.getEditor().getText()))
          .setEnd(HourGlass.parseMillisFromText(inputTo.getEditor().getText()))
          .setTask(inputTaskCombo.getEditor().getText())
          .setComment(inputComment.getText()).build();
      logStorage.insert(log);

      // Resetting controls
      inputComment.setText("");
      hourGlass.restart();
      inputTo.requestFocus();
      inputFrom.requestFocus();
      inputComment.requestFocus();
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  //endregion

  //region Listeners

  StringConverter startDateConverter = new StringConverter<LocalDate>() {
    @Override
    public String toString(LocalDate date) {
      if (date == null) return HourGlass.longFormat.print(DateTime.now());
      DateTime updateTime = new DateTime(hourGlass.getStartMillis()).withDate(
          date.getYear(),
          date.getMonthValue(),
          date.getDayOfMonth()
      );
      return HourGlass.longFormat.print(updateTime);
    }

    @Override
    public LocalDate fromString(String string) {
      try {
        DateTime dateTime = HourGlass.longFormat.parseDateTime(string);
        return LocalDate.of(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
      } catch (IllegalArgumentException e) {
        DateTime oldTime = DateTime.now();
        return LocalDate.of(oldTime.getYear(), oldTime.getMonthOfYear(), oldTime.getDayOfMonth());
      }
    }
  };

  StringConverter endDateConverter = new StringConverter<LocalDate>() {
    @Override
    public String toString(LocalDate date) {
      if (date == null) return HourGlass.longFormat.print(DateTime.now());
      DateTime updateTime = new DateTime(hourGlass.getEndMillis()).withDate(
          date.getYear(),
          date.getMonthValue(),
          date.getDayOfMonth()
      );
      return HourGlass.longFormat.print(updateTime);
    }

    @Override
    public LocalDate fromString(String string) {
      try {
        DateTime dateTime = HourGlass.longFormat.parseDateTime(string);
        return LocalDate.of(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
      } catch (IllegalArgumentException e) {
        DateTime oldTime = DateTime.now();
        return LocalDate.of(oldTime.getYear(), oldTime.getMonthOfYear(), oldTime.getDayOfMonth());
      }
    }
  };

  ChangeListener<String> timeChangeListener = new ChangeListener<String>() {
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue,
                        String newValue) {
      hourGlass.updateTimers(inputFrom.getEditor().getText(), inputTo.getEditor().getText());
      logStorage.setTargetDate(inputFrom.getEditor().getText());
    }
  };

  private HourGlass.Listener hourglassListener = new HourGlass.Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.getEditor().setText(HourGlass.longFormat.print(start));
      inputTo.getEditor().setText(HourGlass.longFormat.print(end));
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, Utils.formatShortDuration(
          duration)));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.getEditor().setText("");
      inputTo.getEditor().setText("");
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, Utils.formatShortDuration(duration)));
    }

    @Override
    public void onTick(final long start, final long end, final long duration) {
      clearError(inputFrom.getEditor());
      clearError(inputTo.getEditor());
      String newFrom = HourGlass.longFormat.print(start);
      if (!newFrom.equals(inputFrom.getEditor().getText()) && !inputFrom.isFocused()) {
        inputFrom.getEditor().setText(newFrom);
      }
      String newTo = HourGlass.longFormat.print(end);
      if (!newTo.equals(inputTo.getEditor().getText()) && !inputTo.isFocused()) {
        inputTo.getEditor().setText(newTo);
      }
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER,
          Utils.formatShortDuration(duration)));
    }

    @Override
    public void onError(HourGlass.Error error) {
      switch (error) {
        case START:
        case END:
          reportError(inputFrom.getEditor());
          reportError(inputTo.getEditor());
          break;
        case DURATION:
          reportError(inputFrom.getEditor());
          reportError(inputTo.getEditor());
          break;
      }
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, error.getMessage()));
    }

  };

  //endregion

  //region Convenience


  /**
   * Adds an indicator as an error for the text field
   *
   * @param tf provided text field
   */
  private void reportError(TextField tf) {
    ObservableList<String> styleClass = tf.getStyleClass();
    if (!styleClass.contains("error"))
      styleClass.add("error");
  }

  /**
   * Removes error indicator for the text field
   *
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

package lt.markmerkk.ui.clock;

import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lt.markmerkk.*;
import lt.markmerkk.entities.*;
import lt.markmerkk.entities.database.interfaces.IExecutor;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.DisplayType;
import lt.markmerkk.utils.IssueSearchAdapter;
import lt.markmerkk.utils.LogFormatters;
import lt.markmerkk.utils.LogUtils;
import lt.markmerkk.utils.SyncController2;
import lt.markmerkk.utils.hourglass.HourGlass;
import lt.markmerkk.utils.tracker.SimpleTracker;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.observables.JavaFxObservable;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by mariusmerkevicius on 12/5/15. Represents the presenter for the clock for logging
 * info.
 */
public class ClockPresenter implements Initializable, IRemoteLoadListener, IDataListener<LocalIssue> {
  public static final Logger logger = LoggerFactory.getLogger(ClockPresenter.class);
  public static final String BUTTON_LABEL_ENTER = "Enter";

  @Inject
  HourGlass hourGlass;
  @Inject
  SyncController2 syncController;
  @Inject
  LogStorage logStorage;
  @Inject
  BasicIssueStorage issueStorage;
  @Inject
  IExecutor dbProdExecutor;
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
  @FXML
  Button buttonRefresh;
  @FXML
  Button buttonSettings;
  @FXML
  Text outputJQL;

  @FXML ProgressIndicator taskLoadIndicator;
  @FXML ComboBox<LocalIssue> inputTaskCombo;

  IssueSearchAdapter issueSearchAdapter;
  Listener listener;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Main.getComponent().presenterComponent().inject(this);
    issueSearchAdapter = new IssueSearchAdapter(
            settings,
            inputTaskCombo,
            dbProdExecutor,
            outputJQL
    );
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

    inputFrom.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_input_from")));
    inputTo.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_input_to")));
    inputTaskCombo.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_search_combo")));
    buttonClock.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_button_startstop")));
    buttonEnter.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_button_enter")));
    buttonRefresh.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_button_refresh")));
    buttonOpen.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_button_open")));
    buttonSettings.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_button_settings")));
    inputComment.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_button_comment")));
    hourGlass.setCurrentDay(DateTime.now());
    hourGlass.setListener(hourglassListener);
    inputFrom.getEditor().textProperty().addListener(timeChangeListener);
    inputTo.getEditor().textProperty().addListener(timeChangeListener);
    inputFrom.setConverter(startDateConverter);
    inputTo.setConverter(endDateConverter);

    Platform.runLater(() -> {
      buttonOpen.setVisible(false);
      buttonOpen.setManaged(false);
    });
    updateUI();

    issueStorage.register(this);
    onLoadChange(syncController.isLoading());
    syncController.addLoadingListener(this);
  }

  @PreDestroy
  public void destroy() {
    issueStorage.unregister(this);
    syncController.removeLoadingListener(this);
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
    syncController.syncIssues();
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
    inputComment.setPromptText((disableElement)
        ? Translation.getInstance().getString("clock_prompt_comment_idle")
        : Translation.getInstance().getString("clock_prompt_comment_running"));
    buttonEnter.setDisable(disableElement);
  }

  /**
   * Gathers data and logs work to a database
   */
  private void logWork() {
    try {
      if (hourGlass.getState() == HourGlass.State.STOPPED)
        throw new IllegalArgumentException(Translation.getInstance().getString("clock_error_timer_not_running"));
      if (!hourGlass.isValid())
        throw new IllegalArgumentException(Translation.getInstance().getString("clock_error_timer_calculation"));
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
      SimpleTracker.getInstance().getTracker().sendEvent(
          SimpleTracker.CATEGORY_BUTTON,
          (logStorage.getDisplayType() == DisplayType.DAY) ? SimpleTracker.ACTION_ENTER_FROM_DAY : SimpleTracker.ACTION_ENTER_FROM_WEEK
      );
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  //endregion

  //region Listeners

  StringConverter startDateConverter = new StringConverter<LocalDate>() {
    @Override
    public String toString(LocalDate date) {
      if (date == null) return LogFormatters.INSTANCE.getLongFormat().print(DateTime.now());
      DateTime updateTime = new DateTime(hourGlass.getStartMillis()).withDate(
          date.getYear(),
          date.getMonthValue(),
          date.getDayOfMonth()
      );
      return LogFormatters.INSTANCE.getLongFormat().print(updateTime);
    }

    @Override
    public LocalDate fromString(String string) {
      try {
        DateTime dateTime = LogFormatters.INSTANCE.getLongFormat().parseDateTime(string);
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
      if (date == null) return LogFormatters.INSTANCE.getLongFormat().print(DateTime.now());
      DateTime updateTime = new DateTime(hourGlass.getEndMillis()).withDate(
          date.getYear(),
          date.getMonthValue(),
          date.getDayOfMonth()
      );
      return LogFormatters.INSTANCE.getLongFormat().print(updateTime);
    }

    @Override
    public LocalDate fromString(String string) {
      try {
        DateTime dateTime = LogFormatters.INSTANCE.getLongFormat().parseDateTime(string);
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
      logStorage.suggestTargetDate(inputFrom.getEditor().getText());
    }
  };

  private HourGlass.Listener hourglassListener = new HourGlass.Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.getEditor().setText(LogFormatters.INSTANCE.getLongFormat().print(start));
      inputTo.getEditor().setText(LogFormatters.INSTANCE.getLongFormat().print(end));
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, LogUtils.INSTANCE.formatShortDuration(
          duration)));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.getEditor().setText("");
      inputTo.getEditor().setText("");
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER, LogUtils.INSTANCE.formatShortDuration(duration)));
    }

    @Override
    public void onTick(final long start, final long end, final long duration) {
      clearError(inputFrom.getEditor());
      clearError(inputTo.getEditor());
      String newFrom = LogFormatters.INSTANCE.getLongFormat().print(start);
      if (!newFrom.equals(inputFrom.getEditor().getText()) && !inputFrom.isFocused()) {
        inputFrom.getEditor().setText(newFrom);
      }
      String newTo = LogFormatters.INSTANCE.getLongFormat().print(end);
      if (!newTo.equals(inputTo.getEditor().getText()) && !inputTo.isFocused()) {
        inputTo.getEditor().setText(newTo);
      }
      buttonEnter.setText(String.format("%s (%s)", BUTTON_LABEL_ENTER,
          LogUtils.INSTANCE.formatShortDuration(duration)));
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

  @Override
  public void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      logger.debug("Clock onLoad: "+loading);
      taskLoadIndicator.setManaged(loading);
      taskLoadIndicator.setVisible(loading);
    });
 }

  @Override
  public void onError(String error) { }

  @Override
  public void onDataChange(@NotNull List<? extends LocalIssue> data) {
    issueSearchAdapter.setTotalIssues(data.size());
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

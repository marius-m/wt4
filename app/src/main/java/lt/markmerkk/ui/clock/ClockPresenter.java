package lt.markmerkk.ui.clock;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lt.markmerkk.*;
import lt.markmerkk.entities.LocalIssue;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.entities.SimpleLogBuilder;
import lt.markmerkk.entities.database.interfaces.IExecutor;
import lt.markmerkk.interactors.IssueSearchInteractorImpl;
import lt.markmerkk.interactors.SyncInteractor;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.mvp.IssueSearchMvp;
import lt.markmerkk.mvp.IssueSearchPresenterImpl;
import lt.markmerkk.mvp.SearchOpenMvp;
import lt.markmerkk.mvp.SearchOpenPresenterImpl;
import lt.markmerkk.utils.AutoCompletionBindingIssues;
import lt.markmerkk.utils.IssueSplitImpl;
import lt.markmerkk.utils.LogFormatters;
import lt.markmerkk.utils.LogUtils;
import lt.markmerkk.utils.hourglass.HourGlass;
import lt.markmerkk.utils.tracker.ITracker;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by mariusmerkevicius on 12/5/15. Represents the presenter for the clock for logging
 * info.
 */
public class ClockPresenter implements Initializable, IRemoteLoadListener,
        IDataListener<LocalIssue>, IssueSearchMvp.View, SearchOpenMvp.View {
  public static final Logger logger = LoggerFactory.getLogger(ClockPresenter.class);

  @Inject
  HourGlass hourGlass;
  @Inject
  SyncInteractor syncInteractor;
  @Inject
  LogStorage logStorage;
  @Inject
  IssueStorage issueStorage;
  @Inject
  IExecutor dbProdExecutor;
  @Inject
  ITracker tracker;
  @Inject
  Application application;
  @Inject
  UserSettings userSettings;

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
  @FXML
  TextField inputTask;

  @FXML ProgressIndicator taskLoadIndicator;

  IssueSearchMvp.Presenter issueSearchPresenter;
  SearchOpenMvp.Presenter searchOpenPresenter;
  IssueSplitImpl issueSplit = new IssueSplitImpl();
  Listener listener;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Main.Companion.getComponent().presenterComponent().inject(this);
    searchOpenPresenter = new SearchOpenPresenterImpl(
            this,
            new HostServicesInteractorImpl(application, userSettings)
    );
    issueSearchPresenter = new IssueSearchPresenterImpl(
            this,
            new IssueSearchInteractorImpl(dbProdExecutor),
            Schedulers.computation(),
            JavaFxScheduler.getInstance()
    );
    AutoCompletionBindingIssues autoCompletionBindingIssues = new AutoCompletionBindingIssues(
            new IssueSearchInteractorImpl(dbProdExecutor),
            inputTask
    );
    inputFrom.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_input_from")));
    inputTo.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_input_to")));
    inputTask.setTooltip(new Tooltip(Translation.getInstance().getString("clock_tooltip_search_combo")));
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

    updateUI();

    onLoadChange(syncInteractor.isLoading());
    syncInteractor.addLoadingListener(this);
    issueSearchPresenter.onAttach();
    issueStorage.register(this);
    inputTask.textProperty().addListener(searchChangeListener);
    searchOpenPresenter.handleInputChange("");
  }

  @PreDestroy
  public void destroy() {
    inputTask.textProperty().removeListener(searchChangeListener);
    issueStorage.unregister(this);
    issueSearchPresenter.onDetach();
    if (hourGlass.getState() == HourGlass.State.RUNNING) {
      try {
        SimpleLog log = new SimpleLogBuilder(DateTime.now().getMillis())
                .setStart(HourGlass.parseMillisFromText(inputFrom.getEditor().getText()))
                .setEnd(HourGlass.parseMillisFromText(inputTo.getEditor().getText()))
                .setTask(
                        issueSplit.split(inputTask.getText()).get(IssueSplitImpl.Companion.getKEY_KEY())
                )
                .setComment(inputComment.getText() + "(abnormal app close)").build();
        logStorage.insert(log);
      } catch (IllegalArgumentException e) {
      }
      hourGlass.stop();
    }
    syncInteractor.removeLoadingListener(this);
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
      searchOpenPresenter.open(inputTask.getText());
  }

  public void onClickSearch() {
    syncInteractor.syncIssues();
    tracker.sendEvent(
            GAStatics.INSTANCE.getCATEGORY_BUTTON(),
            GAStatics.INSTANCE.getACTION_SEARCH_REFRESH()
    );
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
          .setTask(
                  issueSplit.split(inputTask.getText()).get(IssueSplitImpl.Companion.getKEY_KEY())
          )
          .setComment(inputComment.getText()).build();
      logStorage.insert(log);

      // Resetting controls
      inputComment.setText("");
      hourGlass.restart();
      inputTo.requestFocus();
      inputFrom.requestFocus();
      inputComment.requestFocus();
      tracker.sendEvent(
              GAStatics.INSTANCE.getCATEGORY_BUTTON(),
              GAStatics.INSTANCE.getACTION_ENTER()
      );
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  //endregion

  //region Listeners

  ChangeListener<String> searchChangeListener = new ChangeListener<String>() {
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      searchOpenPresenter.handleInputChange(newValue);
    }
  };

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
    }
  };

  private HourGlass.Listener hourglassListener = new HourGlass.Listener() {
    @Override
    public void onStart(long start, long end, long duration) {
      inputFrom.getEditor().setText(LogFormatters.INSTANCE.getLongFormat().print(start));
      inputTo.getEditor().setText(LogFormatters.INSTANCE.getLongFormat().print(end));
      buttonEnter.setText(String.format("%s", LogUtils.INSTANCE.formatShortDuration(
          duration)));
    }

    @Override
    public void onStop(long start, long end, long duration) {
      inputFrom.getEditor().setText("");
      inputTo.getEditor().setText("");
      buttonEnter.setText(String.format("%s", LogUtils.INSTANCE.formatShortDuration(duration)));
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
      buttonEnter.setText(String.format("%s", LogUtils.INSTANCE.formatShortDuration(duration)));
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
      buttonEnter.setText(String.format("%s", error.getMessage()));
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
      taskLoadIndicator.setManaged(loading);
      taskLoadIndicator.setVisible(loading);
    });
 }

  @Override
  public void onError(String error) { }

  @Override
  public void onDataChange(@NotNull List<? extends LocalIssue> data) {
    issueSearchPresenter.recountIssues();
  }

  @Override
  public void showIssues(@NotNull List<? extends LocalIssue> result) { }

  @Override
  public void hideIssues() { }

  @Override
  public void showTotalIssueCount(int count) {
    outputJQL.setText(String.format(Translation.getInstance().getString("clock_jql_info"), count));
  }

  @Override
  public void showOpenButton() {
      buttonOpen.setVisible(true);
      buttonOpen.setManaged(true);
  }

  @Override
  public void hideOpenButton() {
      buttonOpen.setVisible(false);
      buttonOpen.setManaged(false);
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

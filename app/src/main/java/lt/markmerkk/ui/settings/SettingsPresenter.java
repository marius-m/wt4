package lt.markmerkk.ui.settings;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lt.markmerkk.*;
import lt.markmerkk.interactors.SyncInteractor;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.tracker.SimpleTracker;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to edit settings
 */
public class SettingsPresenter implements Initializable, IRemoteLoadListener {
  public static final org.slf4j.Logger logger = LoggerFactory.getLogger(SettingsPresenter.class);

  @Inject
  UserSettings settings;
  @Inject
  SyncInteractor syncInteractor;
  @Inject AutoSync2 autoSync;

  @FXML TextField inputHost, inputUsername, inputJQL;
  @FXML PasswordField inputPassword;
  @FXML TextArea outputLogger;
  @FXML ProgressIndicator outputProgress;
  @FXML Button buttonRefresh, buttonResetJQL;
  @FXML ComboBox<AutoUpdateValue> refreshCombo;

  Appender guiAppender;

  public SettingsPresenter() { }

  @Override public void initialize(URL location, ResourceBundle resources) {
    Main.getComponent().presenterComponent().inject(this);
    SimpleTracker.getInstance().getTracker().sendView(SimpleTracker.VIEW_SETTINGS);
    refreshCombo.setItems(FXCollections.observableList(AvailableAutoUpdate.INSTANCE.getValues()));
    refreshCombo.getSelectionModel().select(AvailableAutoUpdate.INSTANCE.findAutoUpdateValueByMinute(settings.getAutoUpdateMinutes()));
    refreshCombo.valueProperty().addListener(refreshChangeListener);
    refreshCombo.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_autorefresh")));
    inputHost.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_input_hostname")));
    inputUsername.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_input_username")));
    inputPassword.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_input_password")));
    buttonRefresh.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_button_refresh")));
    outputLogger.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_output_console")));
    inputJQL.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_input_jql")));
    buttonResetJQL.setTooltip(new Tooltip(Translation.getInstance().getString("settings_tooltip_button_reset_jql")));

    inputHost.setText(settings.getHost());
    inputUsername.setText(settings.getUsername());
    inputPassword.setText(settings.getPassword());
    inputJQL.setText(settings.getIssueJql());

    guiAppender = new SimpleAppender();
    guiAppender.setLayout(new PatternLayout(Main.LOG_LAYOUT_PROD));
    outputLogger.clear();
    outputLogger.setText(Utils.lastLog());
    outputLogger.positionCaret(outputLogger.getText().length()-1);
    Logger.getRootLogger().addAppender(guiAppender);

    onLoadChange(syncInteractor.isLoading());
    syncInteractor.addLoadingListener(this);
  }

  @PreDestroy
  public void destroy() {
    syncInteractor.removeLoadingListener(this);

    Logger.getRootLogger().removeAppender(guiAppender);
    settings.setHost(inputHost.getText());
    settings.setUsername(inputUsername.getText());
    settings.setPassword(inputPassword.getText());
    settings.setIssueJql(inputJQL.getText());
    guiAppender.close();
  }

  //region Keyboard input

  /**
   * A button event when user clicks on refresh
   */
  public void onClickRefresh() {
    settings.setHost(inputHost.getText());
    settings.setUsername(inputUsername.getText());
    settings.setPassword(inputPassword.getText());
    settings.setIssueJql(inputJQL.getText());
    syncInteractor.syncAll();
    SimpleTracker.getInstance().getTracker().sendEvent(
            SimpleTracker.CATEGORY_BUTTON,
            SimpleTracker.ACTION_SYNC_SETTINGS
    );
  }

  /**
   * A button event when user clicks on reset JQL
   */
  public void onClickResetJQL() {
    inputJQL.setText(Const.INSTANCE.getDEFAULT_JQL_USER_ISSUES());
    settings.setIssueJql(inputJQL.getText());
  }

  //endregion

  //region Listeners

  ChangeListener<AutoUpdateValue> refreshChangeListener = (observable, oldValue, newValue) -> {
    AutoUpdateValue selectedItem = refreshCombo.getSelectionModel().getSelectedItem();
    settings.setAutoUpdateMinutes(selectedItem.getTimeoutMinutes());
  };

  @Override
  public void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      outputProgress.setManaged(loading);
      outputProgress.setVisible(loading);
    });
  }

  @Override
  public void onError(String error) {
    // output log indicates any errors here
  }

  //endregion

  //region Classes

  private class SimpleAppender extends AppenderSkeleton {
    public SimpleAppender() {
      setThreshold(Priority.INFO);
    }

    @Override
    public boolean requiresLayout() { return true; }

    @Override
    public void close() { }

    @Override
    protected void append(LoggingEvent event) {
      Platform.runLater(() -> SettingsPresenter.this.outputLogger.appendText(layout.format(event)));
    }

  }
//
  //endregion

}

package lt.markmerkk.ui.settings;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import javax.inject.Inject;
import lt.markmerkk.AutoSync2;
import lt.markmerkk.JiraSearchSubscriberImpl;
import lt.markmerkk.Main;
import lt.markmerkk.Translation;
import lt.markmerkk.events.StartAllSyncEvent;
import lt.markmerkk.events.StartIssueSyncEvent;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.utils.SyncController;
import lt.markmerkk.utils.SyncEventBus;
import lt.markmerkk.utils.UserSettingsImpl;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.tracker.SimpleTracker;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to edit settings
 */
public class SettingsPresenter implements Initializable, Destroyable, IRemoteLoadListener {

  @Inject
  UserSettingsImpl settings;
  @Inject SyncController syncController;
  @Inject AutoSync2 autoSync;

  @FXML TextField inputHost, inputUsername, inputJQL;
  @FXML PasswordField inputPassword;
  @FXML TextArea outputLogger;
  @FXML ProgressIndicator outputProgress;
  @FXML Button buttonRefresh, buttonResetJQL;
  @FXML ComboBox<String> refreshCombo;

  Appender guiAppender;

  public SettingsPresenter() { }

  @Override public void initialize(URL location, ResourceBundle resources) {
    SimpleTracker.getInstance().getTracker().sendView(SimpleTracker.VIEW_SETTINGS);
    refreshCombo.setItems(autoSync.getSelectionKeys());
    refreshCombo.getSelectionModel().select(autoSync.currentSelection());
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
    guiAppender.setLayout(new PatternLayout(Main.LOG_LAYOUT));
    outputLogger.clear();
    outputLogger.setText(Utils.lastLog());
    outputLogger.positionCaret(outputLogger.getText().length()-1);
    Logger.getRootLogger().addAppender(guiAppender);
    onLoadChange(syncController.isLoading());
    syncController.addLoadingListener(this);
    SyncEventBus.getInstance().getEventBus().register(this);
  }

  @Override public void destroy() {
    SyncEventBus.getInstance().getEventBus().unregister(this);
    syncController.removeLoadingListener(this);
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
    SyncEventBus.getInstance().getEventBus().post(new StartAllSyncEvent());
  }

  /**
   * A button event when user clicks on reset JQL
   */
  public void onClickResetJQL() {
    inputJQL.setText(JiraSearchSubscriberImpl.Companion.getDEFAULT_JQL_USER_ISSUES());
    settings.setIssueJql(inputJQL.getText());
    SyncEventBus.getInstance().getEventBus().post(new StartIssueSyncEvent());
  }

  //endregion

  //region Listeners

  ChangeListener<String> refreshChangeListener = new ChangeListener<String>() {
    @Override
    public void changed(ObservableValue ov, String t, String t1) {
      String selectedItem = refreshCombo.getSelectionModel().getSelectedItem();
      autoSync.setCurrentSelection(selectedItem);
    }
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

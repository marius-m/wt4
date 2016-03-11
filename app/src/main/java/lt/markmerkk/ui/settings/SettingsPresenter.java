package lt.markmerkk.ui.settings;

import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.AutoSync2;
import lt.markmerkk.Main;
import lt.markmerkk.events.StartLogSyncEvent;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.utils.SyncController;
import lt.markmerkk.utils.SyncEventBus;
import lt.markmerkk.utils.UserSettings;
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

  @Inject UserSettings settings;
  @Inject SyncController syncController;
  @Inject AutoSync2 autoSync;

  @FXML TextField inputHost, inputUsername;
  @FXML PasswordField inputPassword;
  @FXML TextArea outputLogger;
  @FXML ProgressIndicator outputProgress;
  @FXML Button buttonRefresh;
  @FXML ComboBox<String> refreshCombo;

  Appender guiAppender;

  public SettingsPresenter() { }

  @Override public void initialize(URL location, ResourceBundle resources) {
    SimpleTracker.getInstance().getTracker().sendView(SimpleTracker.VIEW_SETTINGS);
    refreshCombo.setItems(autoSync.getSelectionKeys());
    refreshCombo.getSelectionModel().select(autoSync.currentSelection());
    refreshCombo.valueProperty().addListener(refreshChangeListener);
    refreshCombo.setTooltip(new Tooltip("Auto refresh timer " +
        "\n\nChanging this will automatically sync with remote after time interval. "));
    inputHost.setTooltip(new Tooltip("JIRA hostname " +
        "\n\nEnter your hostname for the jira. For ex.: https://jira.ito.lt"));
    inputUsername.setTooltip(new Tooltip("JIRA user username " +
        "\n\nEnter username for the user you will be using."));
    inputPassword.setTooltip(new Tooltip("JIRA user password " +
        "\n\nEnter password for the user you will be using."));
    buttonRefresh.setTooltip(new Tooltip("JIRA sync start/cancel" +
        "\n\nTest remote connection by synchronizing with the remote. " +
        "\nWorks the same way as the button in status bar."));
    outputLogger.setTooltip(new Tooltip("JIRA Connection output" +
        "\n\nLog for the remote connection and sync status. " +
        "\nCan be used for testing/checking remote connection problems."));

    inputHost.setText(settings.getHost());
    inputUsername.setText(settings.getUsername());
    inputPassword.setText(settings.getPassword());
    buttonRefresh.setOnMouseClicked(refreshClickListener);

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
    guiAppender.close();
  }

  //region Events

  /**
   * Called when {@link SyncEventBus} calls {@link StartLogSyncEvent}
   * @param event
   */
  @Subscribe
  public void onEvent(StartLogSyncEvent event) {
    if (syncController.isLoading())
      return;
    SimpleTracker.getInstance().getTracker().sendEvent(
        SimpleTracker.CATEGORY_BUTTON,
        SimpleTracker.ACTION_SYNC_SETTINGS
    );
    syncController.sync();
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

  EventHandler<MouseEvent> refreshClickListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      settings.setHost(inputHost.getText());
      settings.setUsername(inputUsername.getText());
      settings.setPassword(inputPassword.getText());
      SyncEventBus.getInstance().getEventBus().post(new StartLogSyncEvent());
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

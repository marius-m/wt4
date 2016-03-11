package lt.markmerkk.ui.status;

import com.google.common.eventbus.Subscribe;
import com.vinumeris.updatefx.UpdateSummary;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.AutoSync2;
import lt.markmerkk.Main;
import lt.markmerkk.events.StartLogSyncEvent;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.IDataListener;
import lt.markmerkk.ui.utils.DisplayType;
import lt.markmerkk.utils.LastUpdateController;
import lt.markmerkk.utils.SyncController;
import lt.markmerkk.utils.SyncEventBus;
import lt.markmerkk.utils.VersionController;
import lt.markmerkk.utils.hourglass.KeepAliveController;
import lt.markmerkk.utils.tracker.SimpleTracker;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, Destroyable, IRemoteLoadListener,
    VersionController.UpgradeListener {
  @Inject BasicLogStorage storage;
  @Inject LastUpdateController lastUpdateController;
  @Inject SyncController syncController;
  @Inject KeepAliveController keepAliveController;
  @Inject AutoSync2 autoSync;
  @Inject VersionController versionController;

  @FXML ProgressIndicator outputProgress;
  @FXML ProgressIndicator versionLoadIndicator;
  @FXML Button buttonRefresh;
  @FXML Button buttonViewToggle;
  @FXML Button buttonToday;
  @FXML Button buttonAbout;

  String total;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    buttonRefresh.setTooltip(new Tooltip("Status" +
        "\n\nTime since last update. Current sum of today's work log." +
        "\n\nPress to activate/cancel synchronization with remote."));
    buttonRefresh.setOnMouseClicked(outputClickListener);
    buttonViewToggle.setTooltip(new Tooltip("Toggle display view" +
        "\n\nToggles the display view."));
    buttonViewToggle.setOnMouseClicked(buttonViewToggleListener);
    buttonToday.setTooltip(new Tooltip("Total" +
        "\n\nTotal work duration."));
    buttonAbout.setTooltip(new Tooltip("About" +
        "\n\nAbout the app." +
        "\nCheck for automatic updates!"));
    buttonAbout.setOnMouseClicked(aboutClickListener);
    syncController.addLoadingListener(this);
    storage.register(loggerListener);
    total = storage.getTotal();

    updateStatus();
    onLoadChange(syncController.isLoading());
    keepAliveController.setListener(keepAliveListener);
    versionController.addListener(this);

    SyncEventBus.getInstance().getEventBus().register(this);
  }

  @Override public void destroy() {
    versionController.removeListener(this);
    syncController.removeLoadingListener(this);
    storage.unregister(loggerListener);
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
        SimpleTracker.ACTION_SYNC_MAIN
    );
    syncController.sync();
  }

  //endregion

  //region Convenience

  /**
   * Convenience method to update current status
   */
  void updateStatus() {
    buttonRefresh.setText(String.format("Last update: %s", lastUpdateController.getOutput()));
    buttonToday.setText(String.format("Total: %s", total));
    buttonViewToggle.setText(String.format("View: %s", storage.getDisplayType().name()));
  }

  //endregion

  //region Listeners

  @Override
  public void onProgressChange(double progressChange) {
    boolean visible = (progressChange > 0.0f && progressChange < 1.0f);
    versionLoadIndicator.setManaged(visible);
    versionLoadIndicator.setVisible(visible);
  }

  @Override
  public void onSummaryUpdate(UpdateSummary updateSummary) {
    if (updateSummary != null && updateSummary.highestVersion > Main.VERSION_CODE) {
      buttonAbout.setText("!");
      return;
    }
    buttonAbout.setText("?");
  }

  EventHandler<MouseEvent> buttonViewToggleListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      if (listener == null) return;
      DisplayType displayType = (storage.getDisplayType() == DisplayType.DAY) ? DisplayType.WEEK : DisplayType.DAY;
      storage.setDisplayType(displayType);
      listener.onDisplayType(displayType);
      updateStatus();
    }
  };

  IDataListener loggerListener = new IDataListener() {
    @Override
    public void onDataChange(ObservableList data) {
      total = storage.getTotal();
      updateStatus();
    }
  };

  EventHandler<MouseEvent> outputClickListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      SyncEventBus.getInstance().getEventBus().post(new StartLogSyncEvent());
    }
  };

  EventHandler<MouseEvent> aboutClickListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      if (listener == null) return;
      listener.onAbout();
    }
  };

  KeepAliveController.Listener keepAliveListener = new KeepAliveController.Listener() {
    @Override
    public void onUpdate() {
      if (!syncController.isLoading() && autoSync.isSyncNeeded())
        syncController.sync();
      updateStatus();
    }
  };

  //endregion

  //region Getters / Setters

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  @Override
  public void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      outputProgress.setManaged(loading);
      outputProgress.setVisible(loading);
    });
    updateStatus();
  }

  @Override
  public void onError(String error) {
    updateStatus();
  }

  //endregion

  //region Classes

  /**
   * Helper listener for interacting with status
   * listener
   */
  public interface Listener {
    /**
     * Callback whenever display type is selected
     * @param type
     */
    void onDisplayType(DisplayType type);

    /**
     * Called when about is pressed
     */
    void onAbout();
  }

  //endregion

}

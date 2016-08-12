package lt.markmerkk.ui.status;

import com.vinumeris.updatefx.UpdateSummary;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.AutoSync2;
import lt.markmerkk.IDataListener;
import lt.markmerkk.Main;
import lt.markmerkk.Translation;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.interactors.KeepAliveInteractor;
import lt.markmerkk.interactors.SyncController;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.LogStorage;
import lt.markmerkk.DisplayType;
import lt.markmerkk.utils.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, IRemoteLoadListener,
    VersionController.UpgradeListener, KeepAliveInteractor.Listener {
  public static final Logger logger = LoggerFactory.getLogger(StatusPresenter.class);

  @Inject
  LogStorage storage;
  @Inject LastUpdateController lastUpdateController;
//  @Inject KeepAliveController keepAliveController;
  @Inject AutoSync2 autoSync;
  @Inject VersionController versionController;
  @Inject
  SyncController syncController;
  @Inject
  KeepAliveInteractor keepAliveInteractor;

  @FXML ProgressIndicator outputProgress;
  @FXML ProgressIndicator versionLoadIndicator;
  @FXML Button buttonRefresh;
  @FXML ToggleButton buttonViewToggle;
  @FXML Button buttonToday;
  @FXML Button buttonAbout;

  String total;

  Listener listener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    Main.getComponent().presenterComponent().inject(this);
    buttonRefresh.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_button_status")));
    buttonViewToggle.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_toggle_view")));
    buttonViewToggle.setOnMouseClicked(buttonViewToggleListener);
    buttonToday.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_button_total")));
    buttonAbout.setTooltip(new Tooltip(Translation.getInstance().getString("status_tooltip_button_about")));
    buttonAbout.setOnMouseClicked(aboutClickListener);
    syncController.addLoadingListener(this);
    total = LogUtils.INSTANCE.formatShortDuration(storage.total());

    updateStatus();
    onLoadChange(syncController.isLoading());
    versionController.addListener(this);
    storage.register(loggerListener);
    keepAliveInteractor.register(this);
  }

  @PreDestroy
  public void destroy() {
    keepAliveInteractor.unregister(this);
    storage.unregister(loggerListener);
    versionController.removeListener(this);
    syncController.removeLoadingListener(this);
  }

  //endregion

  //region Keyboard input

  /**
   * A button event when user clicks on refresh
   */
  public void onClickRefresh() {
    syncController.syncLogs();
  }

  //endregion

  //region Convenience

  /**
   * Convenience method to update current status
   */
  void updateStatus() {
//    buttonRefresh.setText(String.format("Last update: %s", lastUpdateController.getOutput())); // todo : No more update timer output for now.
    buttonToday.setText(LogUtils.INSTANCE.formatShortDuration(storage.total()));
//    buttonViewToggle.setText(String.format("View: %s", storage.getDisplayType().name()));
    buttonViewToggle.setSelected(storage.getDisplayType() == DisplayType.WEEK);
  }

  //endregion

  //region Listeners

//  EventHandler<MouseEvent> outputProgressClickListener = new EventHandler<MouseEvent>() {
//    @Override
//    public void handle(MouseEvent event) {
//      SyncEventBus.getInstance().getEventBus().post(new StartLogSyncEvent());
//    }
//  };

  @Override
  public void onProgressChange(double progressChange) {
    boolean visible = (progressChange > 0.0f && progressChange < 1.0f);
    versionLoadIndicator.setManaged(visible);
    versionLoadIndicator.setVisible(visible);
  }

  @Override
  public void onSummaryUpdate(UpdateSummary updateSummary) {
    if (updateSummary != null && updateSummary.highestVersion > Main.VERSION_CODE) {
      //buttonAbout.setText("!"); // todo : fix this in time, when update is more stable
      return;
    }
    //buttonAbout.setText("?");
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

  IDataListener<SimpleLog> loggerListener = new IDataListener<SimpleLog>() {
    @Override
    public void onDataChange(@NotNull List<? extends SimpleLog> data) {
      total = LogUtils.INSTANCE.formatShortDuration(storage.total());
      updateStatus();
    }
  };

//  EventHandler<MouseEvent> outputClickListener = new EventHandler<MouseEvent>() {
//    @Override
//    public void handle(MouseEvent event) {
//      SyncEventBus.getInstance().getEventBus().post(new StartLogSyncEvent());
//    }
//  };

  EventHandler<MouseEvent> aboutClickListener = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
      if (listener == null) return;
      listener.onAbout();
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
      logger.debug("Status onLoad: "+loading);
      outputProgress.setManaged(loading);
      outputProgress.setVisible(loading);
      updateStatus();
    });
  }

  @Override
  public void onError(String error) {
    updateStatus();
  }

  @Override
  public void update() {
    if (!syncController.isLoading() && autoSync.isSyncNeeded())
      syncController.syncLogs();
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

package lt.markmerkk.ui.status;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.AutoSync2;
import lt.markmerkk.jira.interfaces.WorkerLoadingListener;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.IDataListener;
import lt.markmerkk.utils.LastUpdateController;
import lt.markmerkk.utils.SyncController;
import lt.markmerkk.utils.hourglass.KeepAliveController;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, Destroyable, WorkerLoadingListener {
  @Inject BasicLogStorage storage;
  @Inject LastUpdateController lastUpdateController;
  @Inject SyncController syncController;
  @Inject KeepAliveController keepAliveController;
  @Inject AutoSync2 autoSync;

  @FXML Button outputStatus;
  @FXML ProgressIndicator outputProgress;

  String total;

  @Override public void initialize(URL location, ResourceBundle resources) {
    outputStatus.setTooltip(new Tooltip("Status" +
        "\n\nTime since last update. Current sum of today's work log." +
        "\n\nPress to activate/cancel synchronization with remote."));
    outputStatus.setOnMouseClicked(outputClickListener);
    syncController.addLoadingListener(this);
    storage.register(loggerListener);
    total = storage.getTotal();

    updateStatus();
    onSyncChange(syncController.isLoading());
    keepAliveController.setListener(keepAliveListener);
  }

  @Override public void destroy() {
    syncController.removeLoadingListener(this);
    storage.unregister(loggerListener);
  }

  //region Convenience

  /**
   * Convenience method to update current status
   */
  void updateStatus() {
    outputStatus.setText(String.format("Last update: %s / Today's log: %s", lastUpdateController.getOutput(), total));
  }

  //endregion

  //region Listeners

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
      syncController.sync();
    }
  };

  @Override
  public void onLoadChange(boolean loading) {
    updateStatus();
  }

  @Override
  public void onSyncChange(boolean syncing) {
    Platform.runLater(() -> {
      outputProgress.setManaged(syncing);
      outputProgress.setVisible(syncing);
    });
    updateStatus();
  }

  KeepAliveController.Listener keepAliveListener = new KeepAliveController.Listener() {
    @Override
    public void onUpdate() {
      if (!syncController.isSyncing() && autoSync.isSyncNeeded())
        syncController.sync();
      updateStatus();
    }
  };

  //endregion

}

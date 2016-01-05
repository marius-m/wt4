package lt.markmerkk.ui.status;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.jira.WorkExecutor;
import lt.markmerkk.jira.WorkScheduler2;
import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.WorkerErrorListener;
import lt.markmerkk.jira.interfaces.WorkerLoadingListener;
import lt.markmerkk.jira.interfaces.WorkerOutputListener;
import lt.markmerkk.jira.workers.JiraWorkerLogin;
import lt.markmerkk.jira.workers.JiraWorkerOpenIssues;
import lt.markmerkk.jira.workers.JiraWorkerPullMerge;
import lt.markmerkk.jira.workers.JiraWorkerPushNew;
import lt.markmerkk.jira.workers.JiraWorkerTodayIssues;
import lt.markmerkk.jira.workers.JiraWorkerWorklogForIssue;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.ILoggerListener;
import lt.markmerkk.utils.LastUpdateController;
import lt.markmerkk.utils.SyncController;
import lt.markmerkk.utils.UserSettings;
import lt.markmerkk.utils.Utils;
import lt.markmerkk.utils.hourglass.HourGlass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, Destroyable, WorkerLoadingListener {
  @Inject BasicLogStorage storage;
  @Inject LastUpdateController lastUpdateController;
  @Inject SyncController syncController;

  @FXML TextField outputStatus;
  @FXML ProgressIndicator outputProgress;

  String total;

  @Override public void initialize(URL location, ResourceBundle resources) {
    outputStatus.setOnMouseClicked(outputClickListener);
    syncController.addLoadingListener(this);
    storage.register(loggerListener);
    total = storage.getTotal();
    updateStatus();

    onLoadChange(syncController.isLoading());
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

  ILoggerListener loggerListener = new ILoggerListener() {
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
    Platform.runLater(() -> {
      outputProgress.setManaged(loading);
      outputProgress.setVisible(loading);
    });
  }

  //endregion

}

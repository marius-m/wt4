package lt.markmerkk.ui.status;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.controllers.MainController;
import lt.markmerkk.jira.WorkExecutor;
import lt.markmerkk.jira.WorkScheduler2;
import lt.markmerkk.jira.entities.Credentials;
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
import lt.markmerkk.utils.UserSettings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by mariusmerkevicius on 12/20/15.
 * Represents the presenter to show app status
 */
public class StatusPresenter implements Initializable, Destroyable {
  @Inject UserSettings settings;
  @Inject DBProdExecutor executor;
  @Inject BasicLogStorage storage;
  @Inject WorkExecutor workExecutor;

  @FXML TextField outputStatus;
  @FXML ProgressIndicator outputProgress;

  Log log = LogFactory.getLog(WorkExecutor.class);

  String total;
  String lastUpdate;

  @Override public void initialize(URL location, ResourceBundle resources) {
    outputStatus.setOnMouseClicked(outputClickListener);
    workExecutor.setOutputListener(workerOutputListener);
    workExecutor.setLoadingListener(workerLoadingListener);
    storage.register(loggerListener);
    total = storage.getTotal();
    updateStatus();
  }

  @Override public void destroy() {
    storage.unregister(loggerListener);
  }

  //region Convenience

  /**
   * Called whenever status is clicked
   */
  public void onStatusClick() {
    if (workExecutor.isLoading() || workExecutor.hasMore()) {
      workExecutor.cancel();
      return;
    }
    try {
      Credentials credentials =
          new Credentials(settings.getUsername(), settings.getPassword(),
              settings.getHost());
      workExecutor.executeScheduler(
          new WorkScheduler2(credentials,
              new JiraWorkerLogin(),
              new JiraWorkerPushNew(executor, storage.getTargetDate()),
              new JiraWorkerTodayIssues(storage.getTargetDate()),
              new JiraWorkerWorklogForIssue(settings.getUsername(), storage.getTargetDate()),
              new JiraWorkerPullMerge(executor),
              new JiraWorkerOpenIssues(executor)
          )
      );
    } catch (Exception e) {
//      System.out.println(e.getMessage());
//      e.printStackTrace();
      log.info("Jira: " + e.getMessage());
    }
  }

  /**
   * Convenience method to update current status
   */
  void updateStatus() {
    // fixme : incomplete
    outputStatus.setText(String.format("%s / Update: never", total));
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
      onStatusClick();
    }
  };

  WorkerOutputListener workerOutputListener = new WorkerOutputListener() {
    @Override
    public void onOutput(String message) {
      //System.out.println(message);
    }
  };

  WorkerLoadingListener workerLoadingListener = new WorkerLoadingListener() {
    @Override
    public void onLoadChange(boolean loading) {
      outputProgress.setVisible(loading);
      if (!loading)
        storage.notifyDataChange();
    }
  };

  //endregion

}

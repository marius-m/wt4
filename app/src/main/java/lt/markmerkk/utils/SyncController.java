package lt.markmerkk.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lt.markmerkk.AutoSync;
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
import lt.markmerkk.storage2.BasicLogStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Handles synchronization with jira from other components
 */
public class SyncController {
  @Inject UserSettings settings;
  @Inject DBProdExecutor executor;
  @Inject BasicLogStorage storage;
  @Inject WorkExecutor workExecutor;
  @Inject LastUpdateController lastUpdateController;
  @Inject AutoSync autoSync;

  Log log = LogFactory.getLog(SyncController.class);

  List<WorkerLoadingListener> loadingListenerList = new ArrayList<>();

  @PostConstruct
  public void init() {
    workExecutor.setOutputListener(workerOutputListener);
    workExecutor.setLoadingListener(workerLoadingListener);
    workExecutor.setErrorListener(errorListener);
    autoSync.setListener(autoSyncListener);
    autoSync.schedule(settings.getAutoUpdate());
  }

  /**
   * Main method to start synchronization
   */
  public void sync() {
    if (workExecutor.isLoading() || workExecutor.hasMore()) {
      workExecutor.cancel();
      return;
    }
    lastUpdateController.setError(false);
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
      log.info(e.getMessage());
    }
  }

  //region Getters / Setters

  public void addLoadingListener(WorkerLoadingListener listener) {
    if (listener == null) return;
    loadingListenerList.add(listener);
  }

  public void removeLoadingListener(WorkerLoadingListener listener) {
    if (listener == null) return;
    loadingListenerList.remove(listener);
  }

  public boolean isLoading() {
    return workExecutor.isLoading();
  }

  //endregion

  //region Listeners

  AutoSync.Listener autoSyncListener = new AutoSync.Listener() {
    @Override
    public void onTrigger() {
      if (workExecutor.isLoading() || workExecutor.hasMore())
        return;
      sync();
    }
  };

  WorkerOutputListener workerOutputListener = new WorkerOutputListener() {
    @Override
    public void onOutput(String message) {
      log.info(message);
    }
  };

  WorkerLoadingListener workerLoadingListener = new WorkerLoadingListener() {
    @Override
    public void onLoadChange(boolean loading) {
      lastUpdateController.setLoading(loading);
      if (!loading) {
        storage.notifyDataChange();
        lastUpdateController.refresh();
      }
      for (WorkerLoadingListener workerLoadingListener : loadingListenerList)
        workerLoadingListener.onLoadChange(loading);
    }
  };

  WorkerErrorListener errorListener = new WorkerErrorListener() {
    @Override
    public void onError(String error) {
      lastUpdateController.setError(true);
    }
  };

  //endregion

}

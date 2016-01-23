package lt.markmerkk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.JiraLogExecutor;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.RemoteFetchMerger;
import net.rcarz.jiraclient.WorkLog;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Handles synchronization with jira from other components
 */
public class SyncController {
  @Inject UserSettings settings;
  @Inject DBProdExecutor dbExecutor;
  @Inject BasicLogStorage storage;
//  @Inject WorkExecutor workExecutor;
  @Inject LastUpdateController lastUpdateController;

  JiraLogExecutor jiraLogExecutor;
  List<IRemoteLoadListener> remoteLoadListeners = new ArrayList<>();

  @PostConstruct
  public void init() {
    jiraLogExecutor = new JiraLogExecutor(remoteListener, remoteLoadListener);
    jiraLogExecutor.onStart();
  }

  @PreDestroy
  public void destroy() {
    jiraLogExecutor.onStop();
  }

  /**
   * Main method to start synchronization
   */
  public void sync() {
    if (jiraLogExecutor.isLoading()) {
      jiraLogExecutor.cancel();
      return;
    }
    lastUpdateController.setError(false);
    jiraLogExecutor.asyncRunner(
        settings.getHost(),
        settings.getUsername(),
        settings.getPassword(),
        storage.getTargetDate(),
        storage.getTargetDate().plusDays(1)
    );
  }

  //region Getters / Setters

  public boolean isLoading() {
    return jiraLogExecutor.isLoading();
  }

  public void addLoadingListener(IRemoteLoadListener listener) {
    if (listener == null) return;
    remoteLoadListeners.add(listener);
  }

  public void removeLoadingListener(IRemoteLoadListener listener) {
    if (listener == null) return;
    remoteLoadListeners.remove(listener);
  }

  //endregion

  //region Listeners

  IRemoteListener remoteListener = new IRemoteListener() {

    @Override
    public void onWorklogDownloadComplete(Map<String, List<WorkLog>> remoteLogs) {
      for (String key : remoteLogs.keySet())
        for (WorkLog workLog : remoteLogs.get(key))
          new RemoteFetchMerger(dbExecutor, key, workLog).merge();
      Platform.runLater(() -> {
        storage.notifyDataChange();
      });
    }

    @Override
    public void onError(String error) {
      Platform.runLater(() -> {
        lastUpdateController.setError(true);
      });
    }

    @Override
    public void onCancel() {

    }
  };

  IRemoteLoadListener remoteLoadListener = new IRemoteLoadListener() {
    @Override
    public void onLoadChange(boolean loading) {
      Platform.runLater(() -> {
        lastUpdateController.setLoading(loading);
        if (!loading) {
          storage.notifyDataChange();
          lastUpdateController.refresh();
        }
        for (IRemoteLoadListener remoteListeners : SyncController.this.remoteLoadListeners)
          remoteListeners.onLoadChange(loading);
      });
    }
  };

  //endregion

}

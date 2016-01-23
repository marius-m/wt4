package lt.markmerkk.utils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.JiraLogExecutor;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.storage2.BasicLogStorage;
import net.rcarz.jiraclient.WorkLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  List<IRemoteListener> remoteListeners = new ArrayList<>();

  @PostConstruct
  public void init() {
    jiraLogExecutor = new JiraLogExecutor(remoteListener);
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

  public void addLoadingListener(IRemoteListener listener) {
    if (listener == null) return;
    remoteListeners.add(listener);
  }

  public void removeLoadingListener(IRemoteListener listener) {
    if (listener == null) return;
    remoteListeners.remove(listener);
  }

  //endregion

  //region Listeners
  IRemoteListener remoteListener = new IRemoteListener() {
    @Override
    public void onLoadChange(boolean loading) {
      lastUpdateController.setLoading(loading);
      if (!loading) {
        storage.notifyDataChange();
        lastUpdateController.refresh();
      }
      for (IRemoteListener remoteListeners : SyncController.this.remoteListeners)
        remoteListeners.onLoadChange(loading);
    }

    @Override
    public void onResult(List<WorkLog> remoteLogs) {

    }

    @Override
    public void onError(String error) {
      lastUpdateController.setError(true);
    }

    @Override
    public void onCancel() { }
  };

  //endregion

}

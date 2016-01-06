package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.WorkExecutor;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Represents loading callbacks on {@link WorkExecutor}
 */
public interface WorkerLoadingListener {
  /**
   * Reports loading status
   */
  void onLoadChange(boolean loading);
}

package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.WorkExecutor;

/**
 * Created by mariusmerkevicius on 1/5/16.
 * Represents output callbacks on {@link WorkExecutor}
 */
public interface WorkerErrorListener {

  /**
   * Callback output with error message
   */
  void onError(String error);
}

package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.WorkExecutor;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents all the callbacks on {@link WorkExecutor}
 */
public interface WorkerListener {

  /**
   * A method that indicates user for update status
   * @param message provided message
   */
  void onOutput(String message);

  /**
   * Reports loading status
   */
  void onLoadChange(boolean loading);
}
package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.WorkExecutor;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents output callbacks on {@link WorkExecutor}
 */
public interface WorkerOutputListener {

  /**
   * A method that indicates user for update status
   * @param message provided message
   */
  void onOutput(String message);

}

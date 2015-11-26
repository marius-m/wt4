package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IRemote;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Does the execution of the {@link IRemote} client
 */
public interface IWorker {

  /**
   * Outputs pre execution method to indicate work for the user
   * @return
   */
  String preExecuteMessage();

  /**
   * Main execution method for doing any logic
   * @return
   */
  IResponse execute();
}

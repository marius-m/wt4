package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IRemote;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Does the execution of the {@link IRemote} client
 */
public interface IWorker<T> {

  /**
   * Populates credentials needed for the worker to execute
   * @param credentials
   */
  void populateCredentials(ICredentials credentials);

  /**
   * Populates additional data needed for the worker to execute
   * @param inputData
   */
  void populateInput(T inputData);

  /**
   * An identifier that links {@link IResponse} and {@link IWorker} classes
   * @return
   */
  String tag();

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

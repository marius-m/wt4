package lt.markmerkk.jira.interfaces;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Does the execution of the {@link IRemote} client
 */
public interface IWorker {

  /**
   * Populates credentials needed for the worker to execute
   * @param credentials
   */
  void populateCredentials(ICredentials credentials);

  /**
   * Populates additional data needed for the worker to execute
   * @param inputData
   */
  void populateInput(Object inputData);

  /**
   * An identifier that links {@link IWorkerResult} and {@link IWorker} classes
   * @return
   */
  String tag();

  /**
   * Outputs pre execution method to indicate work for the user
   * @return
   */
  String preExecuteMessage();

  /**
   * Outputs post execution method to indicate work for the user
   * @param entity passed in p
   * @return
   */
  String postExecuteMessage(Object entity);

  /**
   * Main execution method for doing any logic
   * @return
   */
  IWorkerResult execute();
}

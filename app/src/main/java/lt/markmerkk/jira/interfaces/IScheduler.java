package lt.markmerkk.jira.interfaces;

import java.util.LinkedList;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Scheduler that is responsible for scheduling jobs in some sequence to
 * complete a wanted result.
 */
public interface IScheduler {

  /**
   * Returns if scheduler has any more tasks
   * @return
   */
  boolean hasMore();

  /**
   * Credentials used with the scheduled tasks
   * @return
   */
  ICredentials credentials();

  /**
   * Represents scheduler name
   * @return
   */
  String name();

  /**
   * A list of workers that are doing the job execution
   */
  LinkedList<IWorker> workers();

  /**
   * Returns next worker in the list.
   * Might return null if no job is left for execution
   * @return
   */
  IWorker next();

  /**
   * Identifies scheduler that job was completed depending on job response
   * and returns next worker to be executed.
   * @param response execution response.
   */
  IWorker complete(IWorkerResult response) throws IllegalStateException;
}

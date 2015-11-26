package lt.markmerkk.jira.interfaces;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Scheduler that is responsible for scheduling jobs in some sequence to
 * complete a wanted result.
 */
public interface IScheduler {

  /**
   * A list of workers that are doing the job execution
   */
  LinkedList<IWorker> workers();

  /**
   * Returns if all the jobs are complete
   * @return
   */
  boolean isComplete();

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
  IWorker complete(IResponse response) throws IllegalStateException;
}

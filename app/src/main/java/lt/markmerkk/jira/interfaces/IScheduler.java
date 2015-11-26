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
   * Identifies scheduler that job was completed depending on job response
   * @param response
   */
  void complete(IResponse response);
}

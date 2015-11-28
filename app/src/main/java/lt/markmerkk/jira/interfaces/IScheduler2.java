package lt.markmerkk.jira.interfaces;

/**
 * Created by mariusmerkevicius on 11/27/15.
 * Scheduler that handlers input {@link IWorker} to execute it and
 * return handle its output as a {@link IWorkerResult}.
 */
public interface IScheduler2 {

  /**
   * Indicates if execution should be started
   * @return
   */
  boolean shouldExecute();

  /**
   * Starts execution by passing worker that needs to be executed
   * @return
   */
  IWorker nextWorker();

  /**
   * Executes a result handling on separate method, as these functions
   * are executed on separate threads
   * @param result
   */
  void handleResult(IWorkerResult result);

  /**
   * Clears scheduler.
   */
  void reset();

}

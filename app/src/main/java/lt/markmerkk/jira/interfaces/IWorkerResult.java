package lt.markmerkk.jira.interfaces;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents a response entity from {@link IWorker}
 */
public interface IWorkerResult<T> {

  /**
   * An identifier tag, that links {@link IWorkerResult} and {@link IWorker}
   * classes when execution is done.
   * @return
   */
  String tag();

  /**
   * An action log what was done when executing a task in the background
   * @return
   */
  String actionLog();

  /**
   * Network object
   * @return
   */
  T entity();

  /**
   * Returns a flag is execution was successful
   * @return
   */
  boolean isSuccess();

}
package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.entities.JiraJobType;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents a response entity from {@link IWorker}
 */
public interface IResponse<T> {

  /**
   * An identifier tag, that links {@link IResponse} and {@link IWorker}
   * classes when execution is done.
   * @return
   */
  String tag();

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

  /**
   * Returns an output message
   * @return
   */
  String outputMessage();

}
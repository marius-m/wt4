package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.entities.JiraJobType;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents an entity response from network
 */
public interface IJiraResponse<T> {

  /**
   * Job type
   * @return
   */
  JiraJobType type();

  /**
   * Network object
   * @return
   */
  T entity();

  /**
   * Error message
   * @return
   */
  String error();

  /**
   * Success message
   * @return
   */
  String success();

}
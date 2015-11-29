package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents success response
 */
public class SuccessWorkerResult<T> implements IWorkerResult<T> {
  T entity;
  String tag;
  String actionLog;

  /**
   * Success constructor
   * @param tag provided response tag
   * @param entity provided entity with response
   */
  public SuccessWorkerResult(String tag, T entity) {
    this(tag, entity, null);
  }

  public SuccessWorkerResult(String tag, T entity, String actionLog) {
    if (entity == null)
      throw new IllegalArgumentException("Response cannot be initialized without an entity!");
    if (tag == null)
      throw new IllegalArgumentException("Response cannot be initialized without a tag!");
    this.entity = entity;
    this.tag = tag;
    this.actionLog = actionLog;
  }

  @Override public String tag() {
    return tag;
  }

  @Override public String actionLog() {
    return actionLog;
  }

  @Override public T entity() {
    return entity;
  }

  @Override public boolean isSuccess() {
    return true;
  }

}

package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents success response
 */
public class SuccessWorkerResult<T> implements IWorkerResult<T> {
  T entity;
  boolean isSuccess;
  String outputMessage;
  String tag;

  /**
   * Success constructor
   * @param tag provided response tag
   * @param outputMessage provided success message
   * @param entity provided entity with response
   */
  public SuccessWorkerResult(String tag, String outputMessage, T entity) {
    if (entity == null)
      throw new IllegalArgumentException("Response cannot be initialized without an entity!");
    if (outputMessage == null)
      throw new IllegalArgumentException("Response cannot be initialized without a message!");
    if (tag == null)
      throw new IllegalArgumentException("Response cannot be initialized without a tag!");
    this.entity = entity;
    this.outputMessage = outputMessage;
    this.isSuccess = true;
    this.tag = tag;
  }

  @Override public String tag() {
    return tag;
  }

  @Override public T entity() {
    return entity;
  }

  @Override public boolean isSuccess() {
    return isSuccess;
  }

  @Override public String outputMessage() {
    return outputMessage;
  }
}

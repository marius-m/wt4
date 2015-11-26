package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IResponse;

/**
 * Created by mariusmerkevicius on 11/25/15.
 */
public class JiraResponse<T> implements IResponse<T> {

  T entity;
  String outputMessage;
  boolean isSuccess;

  /**
   * Success constructor
   * @param outputMessage
   * @param entity
   */
  public JiraResponse(String outputMessage, T entity) {
    if (entity == null)
      throw new IllegalArgumentException("Success response cannot be initialized without an entity!");
    if (outputMessage == null)
      throw new IllegalArgumentException("Error response cannot be initialized without a message!");
    this.entity = entity;
    this.outputMessage = outputMessage;
    this.isSuccess = true;
  }

  /**
   * Error constructor
   * @param outputMessage
   */
  public JiraResponse(String outputMessage) {
    if (outputMessage == null)
      throw new IllegalArgumentException("Error response cannot be initialized without a message!");
    this.outputMessage = outputMessage;
    this.isSuccess = false;
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

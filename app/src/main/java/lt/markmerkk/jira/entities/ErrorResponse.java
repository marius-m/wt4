package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IResponse;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class ErrorResponse implements IResponse<Object> {
  String outputMessage;
  boolean isSuccess;

  /**
   * Error constructor
   * @param outputMessage
   */
  public ErrorResponse(String outputMessage) {
    if (outputMessage == null)
      throw new IllegalArgumentException("Error response cannot be initialized without a message!");
    this.outputMessage = outputMessage;
    this.isSuccess = false;
  }

  @Override public Object entity() {
    return null;
  }

  @Override public boolean isSuccess() {
    return isSuccess;
  }

  @Override public String outputMessage() {
    return outputMessage;
  }
}

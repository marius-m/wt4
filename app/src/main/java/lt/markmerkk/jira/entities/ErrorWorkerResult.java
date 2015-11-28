package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class ErrorWorkerResult implements IWorkerResult<Object> {
  String tag;
  String outputMessage;
  boolean isSuccess;

  /**
   * Error constructor
   * @param outputMessage
   */
  public ErrorWorkerResult(String tag, String outputMessage) {
    if (outputMessage == null)
      throw new IllegalArgumentException("Response cannot be initialized without a message!");
    if (tag == null)
      throw new IllegalArgumentException("Response cannot be initialized without a tag!");
    this.outputMessage = outputMessage;
    this.isSuccess = false;
    this.tag = tag;
  }

  @Override public String tag() {
    return tag;
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
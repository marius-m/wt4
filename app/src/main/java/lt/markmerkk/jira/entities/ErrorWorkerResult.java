package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class ErrorWorkerResult implements IWorkerResult<Object> {
  String tag;
  String errorMessage;

  /**
   * Error constructor
   * @param errorMessage
   */
  public ErrorWorkerResult(String tag, String errorMessage) {
    if (errorMessage == null)
      throw new IllegalArgumentException("Response cannot be initialized without a message!");
    if (tag == null)
      throw new IllegalArgumentException("Response cannot be initialized without a tag!");
    this.tag = tag;
    this.errorMessage = errorMessage;
  }

  @Override public String tag() {
    return tag;
  }

  @Override public String actionLog() {
    return errorMessage;
  }

  @Override public Object entity() {
    return null;
  }

  @Override public boolean isSuccess() {
    return false;
  }

}

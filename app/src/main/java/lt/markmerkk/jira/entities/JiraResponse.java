package lt.markmerkk.jira.entities;

import lt.markmerkk.jira.interfaces.IJiraResponse;

/**
 * Created by mariusmerkevicius on 11/25/15.
 */
public class JiraResponse<T> implements IJiraResponse<T> {

  JiraJobType jiraJobType;
  T entity;
  String error;
  String successMessage;

  public JiraResponse(T entity, String successMessage) {
    if (entity == null)
      throw new IllegalArgumentException();
    this.entity = entity;
    this.successMessage = successMessage;
  }

  public JiraResponse(String error) {
    if (error == null)
      throw new IllegalArgumentException();
    this.error = error;
  }

  @Override public JiraJobType type() {
    return jiraJobType;
  }

  @Override public T entity() {
    return entity;
  }

  @Override public String error() {
    return error;
  }

  @Override public String success() {
    return successMessage;
  }
}

package lt.markmerkk.jira;

/**
 * Created by mariusmerkevicius on 11/25/15.
 */
public class JiraObject<T> implements IRemoteObject<T> {

  T entity;
  String error;

  public JiraObject(T entity) {
    if (entity == null)
      throw new IllegalArgumentException();
    this.entity = entity;
  }

  public JiraObject(String error) {
    if (error == null)
      throw new IllegalArgumentException();
    this.error = error;
  }

  @Override public T entity() {
    return entity;
  }

  @Override public String error() {
    return error;
  }
}

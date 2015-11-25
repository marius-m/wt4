package lt.markmerkk.jira;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents an entity response from network
 */
public interface IRemoteObject<T> {
  /**
   * Network object
   * @return
   */
  T entity();

  /**
   * Error if there was an error getting the object.
   * @return
   */
  String error();

}
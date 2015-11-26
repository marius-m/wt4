package lt.markmerkk.jira.interfaces;

/**
 * Created by mariusmerkevicius on 11/23/15.
 * Represents the remote server to sync data
 */
public interface IRemote {

  /**
   * Indicates start event on main application
   */
  void onStart();

  /**
   * Indicates a stop event on application
   */
  void onStop();
}

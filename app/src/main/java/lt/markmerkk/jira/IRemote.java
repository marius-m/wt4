package lt.markmerkk.jira;

/**
 * Created by mariusmerkevicius on 11/23/15.
 * Represents the remote server to sync data
 */
public interface IRemote {

  /**
   * Checks if login is valid for the remote
   * @param url provided url to check
   * @param username provided username
   * @param password provided password
   */
  void checkIsLoginValid(String url, String username, String password);

  /**
   * Indicates start event on main application
   */
  void onStart();

  /**
   * Indicates a stop event on application
   */
  void onStop();
}

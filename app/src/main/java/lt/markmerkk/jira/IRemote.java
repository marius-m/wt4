package lt.markmerkk.jira;

/**
 * Created by mariusmerkevicius on 11/23/15.
 * Represents the remote server to sync data
 */
public interface IRemote {
  boolean isConnectionValid(String url, String username, String password);
  void destroy();
}

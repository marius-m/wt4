package lt.markmerkk.jira;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents all the callbacks on {@link JiraExecutor}
 */
public interface JiraListener {
  /**
   * Called when login succeeds
   */
  void onLoginSuccess();

  /**
   * Called when there is an error
   * @param error
   */
  void onError(String error);

  /**
   * Reports loading status
   */
  void onLoadChange(boolean loading);
}

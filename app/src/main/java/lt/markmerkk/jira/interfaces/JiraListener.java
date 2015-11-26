package lt.markmerkk.jira.interfaces;

import lt.markmerkk.jira.Credentials;
import lt.markmerkk.jira.JiraExecutor;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * Represents all the callbacks on {@link JiraExecutor}
 */
public interface JiraListener {

  Credentials getUserCredentials();

  /**
   * Called when execution succeeds
   */
  void onSuccess(String success);
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

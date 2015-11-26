package lt.markmerkk.jira;

import javafx.application.Platform;
import lt.markmerkk.jira.interfaces.IRemote;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.JiraListener;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class JiraExecutor extends TaskExecutor<IResponse> implements IRemote {

  public static final String WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";


  JiraListener listener;

  public JiraExecutor(JiraListener listener) {
    this.listener = listener;
  }

  //region World events

  @Override public void onStop() {
    if (isLoading())
      cancel();
    super.onStop();
  }

  //endregion

  //region Callback

  @Override protected void onResult(final IResponse result) {
    if (listener == null) return;
    Platform.runLater(() -> {
      listener.onOutput(result.outputMessage());
    });
  }

  @Override protected void onLoadChange(final boolean loading) {
    if (listener == null) return;
    Platform.runLater(() -> {
      listener.onLoadChange(loading);
    });
  }

  //endregion

  public void login() {
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin(listener.getUserCredentials());
    if (listener != null)
      listener.onOutput(jiraWorkerLogin.preExecuteMessage());
    executeInBackground(jiraWorkerLogin::execute);
  }

  //region Convenience


  //endregion

}

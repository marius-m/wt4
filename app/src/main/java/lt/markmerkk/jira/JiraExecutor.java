package lt.markmerkk.jira;

import javafx.application.Platform;
import lt.markmerkk.jira.interfaces.IJiraResponse;
import lt.markmerkk.jira.interfaces.IRemote;
import lt.markmerkk.jira.interfaces.JiraListener;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class JiraExecutor extends TaskExecutor<IJiraResponse> implements IRemote {

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

  @Override protected void onResult(IJiraResponse result) {
    Platform.runLater(() -> {
      if (listener == null) return;
      if (result.error() != null) {
        listener.onError(result.error());
        return;
      }
      if (result.success() != null)
        listener.onSuccess(result.success());

      //if (result.entity() instanceof User) {
      //  listener.onSuccessLogin();
      //  worklogsForToday();
      //}
      //if (result.entity() instanceof SearchResult) listener.onTodayWorklog();
    });
  }

  @Override protected void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      if (listener == null) return;
      listener.onLoadChange(loading);
    });
  }

  //endregion

  public void login() {
    executeInBackground(() -> {
      return new JiraWorkerLogin(listener.getUserCredentials()).execute();
    });
  }

  //region Convenience


  //endregion

}

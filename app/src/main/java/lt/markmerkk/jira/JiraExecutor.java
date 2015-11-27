package lt.markmerkk.jira;

import javafx.application.Platform;
import lt.markmerkk.jira.interfaces.IRemote;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IScheduler;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.JiraListener;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class JiraExecutor extends TaskExecutor<IResponse> implements IRemote {

  public static final String WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";


  JiraListener listener;
  IScheduler scheduler;

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
    if (result == null) return;
    listener.onOutput(result.outputMessage());
    if (!result.isSuccess()) return;
    if (scheduler == null) return;
    scheduler.complete(result);
    if (!scheduler.hasMore()) return;
    executeScheduler(scheduler);
  }

  @Override protected void onLoadChange(final boolean loading) {
    if (listener == null) return;
    listener.onLoadChange(loading);
  }

  //endregion

  /**
   * Executes a scheduler defined jobs
   * @param scheduler
   */
  public void executeScheduler(IScheduler scheduler) {
    if (scheduler == null) return;
    if (!scheduler.hasMore()) return;
    this.scheduler = scheduler;
    IWorker nextWorker = scheduler.next();
    execute(nextWorker);
  }

  //region Convenience

  void execute(IWorker worker) {
    if (worker == null) return;
    if (listener != null) listener.onOutput(worker.preExecuteMessage());
    executeInBackground(worker::execute);
  }

  //endregion

}

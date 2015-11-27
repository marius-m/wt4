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

  JiraListener listener;
  IScheduler scheduler;

  public JiraExecutor(JiraListener listener) {
    this.listener = listener;
  }

  //region Public

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

  /**
   * Returns if there are more jobs in the list
   * @return
   */
  public boolean hasMore() {
    if (scheduler == null) return false;
    return (scheduler.hasMore());
  }

  //endregion

  //region Convenience

  /**
   * Reports output message to the outside
   * @param message
   */
  private void reportOutput(String message) {
    if (listener != null) listener.onOutput(message);
  }

  /**
   * Executes a provided worker in the background
   * @param worker
   */
  void execute(IWorker worker) {
    if (worker == null) return;
    reportOutput(worker.preExecuteMessage());
    executeInBackground(worker::execute);
  }

  //endregion

  //region Callback

  @Override protected void onCancel() {
    scheduler = null;
    reportOutput("Cancelling");
  }

  @Override protected void onReady() {
    if (scheduler == null) return;
    if (!scheduler.hasMore()) {
      reportOutput("Finished " + scheduler.name());
      return;
    }
    executeScheduler(scheduler);
  }

  @Override protected void onResult(final IResponse result) {
    if (result == null) return;
    reportOutput(result.outputMessage());
    if (!result.isSuccess()) {
      scheduler = null;
      return;
    }
    if (result.entity() != null)
      reportOutput(scheduler.next().postExecuteMessage(result.entity()));
    if (scheduler == null) return;
    scheduler.complete(result);
  }

  @Override protected void onLoadChange(final boolean loading) {
    if (listener != null)
      listener.onLoadChange(loading);
  }

  //endregion

  //region World events

  @Override public void onStop() {
    if (isLoading())
      cancel();
    super.onStop();
  }

  //endregion

}

package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IRemote;
import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.WorkerListener;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class WorkExecutor extends TaskExecutor<IWorkerResult> implements IRemote {

  WorkerListener listener;
  IScheduler2 scheduler;

  public WorkExecutor(WorkerListener listener) {
    this.listener = listener;
  }

  //region Public

  /**
   * Executes a currentSchedulerOrEmptyOne defined jobs
   * @param scheduler
   */
  public void executeScheduler(IScheduler2 scheduler) {
    this.scheduler = scheduler;
    if (!currentSchedulerOrEmptyOne().shouldExecute()) return;
    IWorker worker = currentSchedulerOrEmptyOne().nextWorker();
    reportOutput(worker.preExecuteMessage());
    executeInBackground(worker::execute);
  }

  /**
   * Returns if there are more jobs in the list
   * @return
   */
  public boolean hasMore() {
    return (currentSchedulerOrEmptyOne().shouldExecute());
  }

  /**
   * Wrapper getter for always returning a scheduler instance
   * @return
   */
  public IScheduler2 currentSchedulerOrEmptyOne() {
    if (scheduler == null)
      scheduler = new NullWorkScheduler2();
    return scheduler;
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

  //endregion

  //region Callback

  @Override protected void onCancel() {
    scheduler.reset();
  }

  @Override protected void onResult(final IWorkerResult result) {
    if (result != null) reportOutput(result.outputMessage());
    currentSchedulerOrEmptyOne().handleResult(result);
    executeScheduler(currentSchedulerOrEmptyOne());
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

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
   * Executes a scheduler defined jobs
   * @param scheduler
   */
  public void executeScheduler(IScheduler2 scheduler) {
    this.scheduler = scheduler;
    if (!scheduler().shouldExecute()) return;
    execute(scheduler().nextWorker());
  }

  /**
   * Returns if there are more jobs in the list
   * @return
   */
  public boolean hasMore() {
    return (scheduler().shouldExecute());
  }

  /**
   * Wrapper getter for always returning an instance
   * @return
   */
  public IScheduler2 scheduler() {
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
  }

  @Override protected void onResult(final IWorkerResult result) {
    reportOutput(result.outputMessage());
    scheduler().handleResult(result);
    executeScheduler(scheduler());
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

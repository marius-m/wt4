package lt.markmerkk.jira;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lt.markmerkk.jira.interfaces.IRemote;
import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorkReporter;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.WorkerListener;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class WorkExecutor extends TaskExecutor2<IWorkerResult> implements IRemote {

  WorkerListener listener;
  IScheduler2 scheduler;
  IWorkReporter reporter;

  public WorkExecutor(IWorkReporter reporter, WorkerListener listener) {
    this.listener = listener;
    if (reporter == null)
      reporter = new NullWorkReporter();
    this.reporter = reporter;
  }

  public WorkExecutor() {
//    this.listener = listener;
//    if (reporter == null)
      reporter = new NullWorkReporter();
//    this.reporter = reporter;
  }

  //region Public

  /**
   * Executes a currentSchedulerOrEmptyOne defined jobs
   * @param scheduler
   */
  public void executeScheduler(IScheduler2 scheduler) {
    this.scheduler = scheduler;
    if (!currentSchedulerOrEmptyOne().shouldExecute()) {
      reportOutputSimple("Sync complete!");
      return;
    }
    IWorker worker = currentSchedulerOrEmptyOne().nextWorker();
    reportStart(worker);
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
  private void reportOutputSimple(String message) {
    if (listener != null) listener.onOutput(reporter.reportMessage(message));
  }

  /**
   * Reports output message to the outside
   */
  private void reportStart(IWorker worker) {
    if (listener != null) listener.onOutput(reporter.reportWorkStart(worker));
  }

  /**
   * Reports output message to the outside
   */
  private void reportEnd(IWorker worker, IWorkerResult result) {
    if (listener != null) listener.onOutput(reporter.reportWorkEnd(worker, result));
  }

  public void setListener(WorkerListener listener) {
    this.listener = listener;
  }

  //endregion

  //region Callback

  @Override protected void onCancel() {
    scheduler.reset();
    onLoadChange(false);
  }

  @Override protected void onReady() {
    executeScheduler(currentSchedulerOrEmptyOne());
  }

  @Override protected void onResult(final IWorkerResult result) {
    if (result != null) reportEnd(currentSchedulerOrEmptyOne().nextWorker(), result);
    currentSchedulerOrEmptyOne().handleResult(result);
  }

  @Override protected void onLoadChange(final boolean loading) {
    //boolean isNotLoading = !loading && !hasMore();
    if (listener != null)
      listener.onLoadChange(isLoading());
  }

  //endregion

  //region World events

  @PostConstruct
  @Override
  public void onStart() {
    super.onStart();
  }

  @PreDestroy
  @Override public void onStop() {
    if (isLoading())
      cancel();
    super.onStop();
  }

  //endregion

}

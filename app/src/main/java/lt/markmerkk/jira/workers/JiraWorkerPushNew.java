package lt.markmerkk.jira.workers;

import lt.markmerkk.PushNewMerger;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerPushNew extends JiraWorker {
  public static final String TAG = "WORKLOG_PUSH";

  private final IExecutor executor;
  private final DateTime targetDate;

  public JiraWorkerPushNew(IExecutor executor, DateTime targetDate) {
    this.targetDate = targetDate;
    this.executor = executor;
  }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    PushNewMerger merger = new PushNewMerger(executor, client, targetDate);
    return new SuccessWorkerResult<>(TAG, merger.merge());
  }

  @Override public void populateInput(IWorkerResult result) { }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Uploading worklogs...";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      return result.entity() + "\n" + "Success pushing changes to jira!\n";
    }
    return "Unknown internal error!";
  }

}

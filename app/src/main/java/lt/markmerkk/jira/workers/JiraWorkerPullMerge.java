package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.domain.Worklog;
import java.util.List;
import java.util.Map;
import lt.markmerkk.PullMerger;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.storage2.database.interfaces.IExecutor;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
// fixme no tests dirty code
public class JiraWorkerPullMerge extends JiraWorker {
  public static final String TAG = "WORKLOG_PULL";

  private final IExecutor executor;
  private Map<String, List<Worklog>> worklogMap;

  public JiraWorkerPullMerge(IExecutor executor) {
    this.executor = executor;
  }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    if (worklogMap == null) return new ErrorWorkerResult(TAG, "Error getting worklog!");
    String actionLog = (worklogMap.size() == 0) ? "No issues to update!" : "Updating local database...\n";
    for (String key : worklogMap.keySet()) {
      List<Worklog> logs = worklogMap.get(key);
      for (Worklog log : logs) {
        PullMerger merger = new PullMerger(executor, key, log);
        actionLog += "    "+merger.merge()+"\n";
      }
    }
    return new SuccessWorkerResult<>(TAG, "Success!", actionLog);
  }

  @Override public void populateInput(IWorkerResult result) {
    if (result.entity() instanceof Map)
      worklogMap = (Map<String, List<Worklog>>) result.entity();
  }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Merging worklogs...";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      return result.actionLog()+"Success merging database!";
    }
    return "Unknown internal error!";
  }

}

package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.InsertJob;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerWorklogMerge extends JiraWorker {
  public static final String TAG = "WORKLOG_MERGE";

  private final IExecutor executor;
  private Map<String, List<Worklog>> worklog;

  public JiraWorkerWorklogMerge(IExecutor executor) {
    this.executor = executor;
  }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    if (worklog == null) return new ErrorWorkerResult(TAG, "Error getting worklog!");
    String actionLog = "  Updating local database...\n";
    for (String key : worklog.keySet()) {
      actionLog += "    Updating task "+key+"\n";
      List<Worklog> logs = worklog.get(key);
      for (Worklog log : logs) {
        SimpleLog simpleLog = new SimpleLogBuilder(key, log).build();
        actionLog += "      Adding new log "+simpleLog+"\n";
        executor.execute(new InsertJob(SimpleLog.class, simpleLog));
      }
    }
    return new SuccessWorkerResult<>(TAG, actionLog);
  }

  @Override public void populateInput(IWorkerResult result) {
    if (result.entity() instanceof Map)
      worklog = (Map<String, List<Worklog>>) result.entity();
  }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Merging worklogs into local database...";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      return result.entity()+"Success merging to local database!";
    }
    return "Unknown internal error!";
  }

}

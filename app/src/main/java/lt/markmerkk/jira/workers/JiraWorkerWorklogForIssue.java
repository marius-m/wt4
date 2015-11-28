package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.util.concurrent.Promise;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerWorklogForIssue extends JiraWorker {
  public static final String TAG = "WORKLOG_FOR_ISSUE";

  SearchResult searchResult;
  public JiraWorkerWorklogForIssue() { }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    if (searchResult == null) return new ErrorWorkerResult(TAG, "Error getting search result!");
    Map<String, List<Worklog>> todayLogs = new HashMap<>();
    for (Issue issueLink : searchResult.getIssues()) {
      Issue issue = client.getIssueClient().getIssue(issueLink.getKey()).claim();
      Promise<List<Worklog>> worklogPromise =
          client.getIssueWorklogRestClient().getIssueWorklogs(issue);
      todayLogs.put(issue.getKey(), worklogPromise.claim());
    }
    return new SuccessWorkerResult<>(TAG, todayLogs);
  }

  @Override public void populateInput(IWorkerResult result) {
    if (result.entity() instanceof SearchResult)
      searchResult = (SearchResult) result.entity();
  }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Getting worklog for the issues";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      if (!(result.entity() instanceof Map)) return "Internal error: Result of wrong type!";
      Map<String, List<Worklog>> map = (Map<String, List<Worklog>>) result.entity();
      String message = "  Success: Here is today worklog of worked issues: \n";
      for (String key : map.keySet()) {
        message += "    Log for "+key+"\n";
        List<Worklog> logs = map.get(key);
        for (Worklog log : logs)
          message += "      "+"["+log.getMinutesSpent()+"]"+log.getComment()+"\n";
      }
      return message;
    }
    return "Unknown internal error!";
  }

}

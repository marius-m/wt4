package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.util.concurrent.Promise;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerWorklogForIssue extends JiraWorker {
  public static final String TAG = "WORKLOG_FOR_ISSUE";
  private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy/MM/dd");
  private SearchResult searchResult;
  private final DateTime fromDate;
  private final DateTime toDate;

  public JiraWorkerWorklogForIssue(DateTime searchDate) {
    if (searchDate == null)
      throw new IllegalArgumentException("Illegal input date!");
    fromDate = searchDate;
    toDate = searchDate.plusDays(1);
  }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    if (searchResult == null) return new ErrorWorkerResult(TAG, "Error getting search result!");
    Map<String, List<Worklog>> todayLogs = new HashMap<>();
    for (Issue issueLink : searchResult.getIssues()) {
      Issue issue = client.getIssueClient().getIssue(issueLink.getKey()).claim();
      List<Worklog> allWorkLogs =
          client.getIssueWorklogRestClient().getIssueWorklogs(issue).claim();
      List<Worklog> searchDateWorklog = new ArrayList<>();
      // Filtering out the worklog
      for (Worklog allWorkLog : allWorkLogs)
        if (allWorkLog.getStartDate().isAfter(fromDate) && allWorkLog.getStartDate().isBefore(toDate))
          searchDateWorklog.add(allWorkLog);
      todayLogs.put(issue.getKey(), searchDateWorklog);
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
    return "Getting worklog for the issues...";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      if (!(result.entity() instanceof Map)) return "Internal error: Result of wrong type!";
      Map<String, List<Worklog>> map = (Map<String, List<Worklog>>) result.entity();
      String message = "Success! ";
      message += (map.size() > 0) ? "Work log for worked issues: \n" : "Did not work on any issues.";
      for (String key : map.keySet()) {
        message += "    Log for "+key+"\n";
        List<Worklog> logs = map.get(key);
        for (Worklog log : logs)
          //message += "      "+"["+log.getMinutesSpent()+"] "+log.getComment()+"\n";
          message += "      "+"["+log.getSelf()+"] "+log.getComment()+"\n";
      }
      return message;
    }
    return "Unknown internal error!";
  }

}

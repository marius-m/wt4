package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerWorklogForTheIssues extends JiraWorker {
  public static final String WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";

  public static final String TAG = "SEARCH_WORKLOG_TODAY";

  public JiraWorkerWorklogForTheIssues() { }

  @Override protected IWorkerResult executeRequest(JiraRestClient client) {
    //for (Issue issueLink : searchResult.getIssues()) {
    //  Issue issue = client.getIssueClient().getIssue(issueLink.getKey()).claim();
    //  System.out.println("Issue: " + issue.getKey());
    //  Promise<List<Worklog>> worklogPromise =
    //      client.getIssueWorklogRestClient().getIssueWorklogs(issue);
    //  List<Worklog> workLogs = worklogPromise.claim();
    //  for (Worklog workLog : workLogs) {
    //    System.out.println("Worklog: " + workLog);
    //  }
    //}

    //SuccessResponse<SearchResult> searchResultForToday =
    //    new SuccessResponse<>(tag(), "Job search complete!",
    //        client.getSearchClient().searchJql(JQL_WORKLOG_FOR_TODAY).claim());
    return null;
  }

  @Override public void populateInput(IWorkerResult result) {

  }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Finding jobs that were done for today";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    return null;
  }

}

package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerTodayIssues extends JiraWorker {
  public static final String JQL_WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";

  public static final String TAG = "SEARCH_ISSUES_TODAY";

  public JiraWorkerTodayIssues() { }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    SuccessWorkerResult<SearchResult>
        searchResultForToday = new SuccessWorkerResult<>(tag(),
        client.getSearchClient().searchJql(JQL_WORKLOG_FOR_TODAY).claim());
    return searchResultForToday;
  }

  @Override public void populateInput(IWorkerResult result) {
    System.out.println("Populating data: "+ result);
  }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Finding jobs that were done for today";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      if (!(result.entity() instanceof SearchResult)) return "Internal error: Result of wrong type!";
      SearchResult searchResult = (SearchResult) result.entity();
      String message = "  Success: Worked on these issues: \n";
      for (Issue issue : searchResult.getIssues())
        message += issue.getKey() + "\n";
      return message;
    }
    return "Unknown internal error!";
  }
}

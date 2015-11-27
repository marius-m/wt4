package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.SuccessResponse;
import lt.markmerkk.jira.interfaces.IResponse;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerSearchWorklogForToday extends JiraWorker<SearchResult> {
  public static final String WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";

  public static final String LOGIN = "SEARCH_WORKLOG_TODAY";

  public JiraWorkerSearchWorklogForToday() { }

  @Override protected IResponse executeRequest(JiraRestClient client) {
    SuccessResponse<SearchResult> searchResultForToday = new SuccessResponse<>(tag(), "Job search complete!",
        client.getSearchClient().searchJql(WORKLOG_FOR_TODAY).claim());
    return searchResultForToday;
  }

  @Override public void populateInput(SearchResult inputData) {

  }

  @Override public String tag() {
    return LOGIN;
  }

  @Override public String preExecuteMessage() {
    return "Finding jobs that were done for today";
  }

  @Override public String postExecuteMessage(SearchResult entity) {
    if (entity == null) return "Unknown";
    String message = "Today worked on these issues: \n";
    for (Issue issue : entity.getIssues())
      message += issue.getProject().getName()+" / "+issue.getKey()+" / "+issue.getSummary()+"\n";
    return message;
  }
}

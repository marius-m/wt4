package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.interfaces.IWorkerResult;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerSearchWorklogForToday extends JiraWorker {
  public static final String WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";

  public static final String LOGIN = "SEARCH_WORKLOG_TODAY";

  public JiraWorkerSearchWorklogForToday() { }

  @Override protected IWorkerResult executeRequest(JiraRestClient client) {
    SuccessWorkerResult<SearchResult>
        searchResultForToday = new SuccessWorkerResult<>(tag(),
        client.getSearchClient().searchJql(WORKLOG_FOR_TODAY).claim());
    return searchResultForToday;
  }

  @Override public void populateInput(Object inputData) {
    System.out.println("Populating data: "+inputData);
  }

  @Override public String tag() {
    return LOGIN;
  }

  @Override public String preExecuteMessage() {
    return "Finding jobs that were done for today";
  }

  @Override public String postExecuteMessage(Object entity) {
    //if (entity == null) return "Unknown";
    //String message = "Today worked on these issues: \n";
    //for (Issue issue : entity.getIssues())
    //  message += issue.getProject().getName()+" / "+issue.getKey()+" / "+issue.getSummary()+"\n";
    //return message;
    return null;
  }
}

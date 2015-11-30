package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import lt.markmerkk.jira.JiraWorker;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleIssueBuilder;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.storage2.jobs.QueryJob;
import lt.markmerkk.storage2.jobs.UpdateJob;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Tries to check if login is valid for the user
 */
public class JiraWorkerOpenIssues extends JiraWorker {
  public static final String TAG = "SEARCH_OPEN";
  public static final String JQL_ASSIGNED_ISSUES =
      "status in (Open, \"In Progress\", \"To Do\") AND assignee in (currentUser())";
  private final IExecutor executor;

  public JiraWorkerOpenIssues(IExecutor executor) {
    if (executor == null)
      throw new IllegalArgumentException("Cannot function without executor!");
    this.executor = executor;
  }

  @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
    String message = "Success! ";
    int startIndex = 0;
    int max = 50;
    int total = 0;
    do {
      SearchResult searchResult = client.getSearchClient()
          .searchJql(JQL_ASSIGNED_ISSUES, max, startIndex, null).claim();
      startIndex += max;
      max = searchResult.getMaxResults();
      total = searchResult.getTotal();

      for (Issue issue : searchResult.getIssues()) {
        QueryJob<SimpleIssue> queryJob =
            new QueryJob<>(SimpleIssue.class, () -> "key = \"" + issue.getKey() + "\"");
        executor.execute(queryJob);
        if (queryJob.result() == null) {
          SimpleIssue issueLog = new SimpleIssueBuilder(issue).build();
          message += "Creating new issue: " + issueLog + "\n";
          executor.execute(new InsertJob(SimpleIssue.class, issueLog));
        } else {
          SimpleIssue issueLog = new SimpleIssueBuilder(queryJob.result(), issue).build();
          message += "Updating old issue: " + issueLog + "\n";
          executor.execute(new UpdateJob(SimpleIssue.class, issueLog));
        }
      }
    } while (total > startIndex);
    return new SuccessWorkerResult<>(tag(), message, message);
  }

  @Override public void populateInput(IWorkerResult result) {
    System.out.println("Populating data: "+ result);
  }

  @Override public String tag() {
    return TAG;
  }

  @Override public String preExecuteMessage() {
    return "Finding all issues user is working on!";
  }

  @Override public String postExecuteMessage(IWorkerResult result) {
    if (super.postExecuteMessage(result) != null) return super.postExecuteMessage(result);
    if (result instanceof SuccessWorkerResult) {
      //if (!(result.entity() instanceof SearchResult)) return "Internal error: Result of wrong type!";
      return result.actionLog();
    }
    return "Unknown internal error!";
  }
}

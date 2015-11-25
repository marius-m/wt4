package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javafx.application.Platform;
import lt.markmerkk.jira.extend_base.AsynchronousJiraRestClientFactoryPlus;
import lt.markmerkk.jira.extend_base.IssueWorklogsRestClient;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;

/**
 * Created by mariusmerkevicius on 11/25/15.
 * A jira executor for background processes
 */
public class JiraExecutor extends TaskExecutor<JiraObject> implements IRemote {

  public static final String WORKLOG_FOR_TODAY =
      "assignee = currentUser() AND worklogDate >= \"2015/11/19\" && worklogDate <= \"2015/11/20\"";

  String username;
  String password;
  String url;

  JiraRestClientPlus client;

  JiraListener listener;

  public JiraExecutor(JiraListener listener) {
    this.listener = listener;
  }

  //region World events

  @Override public void onStart() {
    super.onStart();
  }

  @Override public void onStop() {
    if (isLoading())
      cancel();
    close();
    super.onStop();
  }

  //endregion

  //region Callback

  @Override protected void onResult(JiraObject result) {
    Platform.runLater(() -> {
      if (listener == null) return;
      if (result.error() != null) {
        listener.onError(result.error());
        return;
      }
      if (result.entity() instanceof User) {
        listener.onSuccessLogin();
        worklogsForToday();
      }
      if (result.entity() instanceof SearchResult) listener.onTodayWorklog();
    });
  }

  @Override protected void onLoadChange(boolean loading) {
    Platform.runLater(() -> {
      if (listener == null) return;
      listener.onLoadChange(loading);
    });
  }

  //endregion

  /**
   * Checks login validation
   * @param url provided url
   * @param username provided username
   * @param password provided password
   */
  public void checkIsLoginValid(String url, String username, String password) {
    executeInBackground(() -> {
      try {
        this.username = username;
        this.password = password;
        this.url = url;

        AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
        client = factory.createWithBasicHttpAuthentication(new URI(url), username, password);

        return new JiraObject<User>(client.getUserClient().getUser(username).claim());
      } catch (URISyntaxException e) {
        return new JiraObject("Error: "+e.getMessage());
      } catch (RestClientException e) {
        return new JiraObject("Error: "+e.getCause().toString());
      } catch (IllegalArgumentException e) {
        return new JiraObject("Error: "+e.getMessage());
      } finally {
        close();
      }
    });
  }

  /**
   * Fetches all the worklogs for today
   */
  public void worklogsForToday() {
    executeInBackground(() -> {
      try {
        AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
        client = factory.createWithBasicHttpAuthentication(new URI(url), username, password);

        SearchResult searchResult = client.getSearchClient().searchJql(WORKLOG_FOR_TODAY).claim();
        for (Issue issueLink : searchResult.getIssues()) {
          Issue issue = client.getIssueClient().getIssue(issueLink.getKey()).claim();
          System.out.println("Issue: "+issue.getKey());
          Promise<List<Worklog>> worklogPromise = client.getIssueWorklogRestClient().getIssueWorklogs(
              issue);
          List<Worklog> workLogs = worklogPromise.claim();
          for (Worklog workLog : workLogs) {
            System.out.println("Worklog: "+workLog);
          }
        }

        return new JiraObject<SearchResult>(searchResult);
      } catch (URISyntaxException e) {
        return new JiraObject("Error: "+e.getMessage());
      } catch (RestClientException e) {
        return new JiraObject("Error: "+e.getCause().toString());
      } catch (IllegalArgumentException e) {
        return new JiraObject("Error: "+e.getMessage());
      } finally {
        close();
      }
    });
  }

  //region Convenience

  /**
   * Closes any jira connection
   */
  private void close() {
    try {
      if (client != null)
        client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //endregion

}

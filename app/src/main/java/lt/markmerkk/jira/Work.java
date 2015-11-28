package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.WorkerListener;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class Work extends WorkExecutor {


  public Work(WorkerListener listener) {
    super(listener);
  }


  //region Execution

  ///**
  // * Checks login validation
  // * @param url provided url
  // * @param username provided username
  // * @param password provided password
  // */
  //public void checkIsLoginValid(String url, String username, String password) {
  //  executeInBackground(() -> {
  //    try {
  //      this.username = username;
  //      this.password = password;
  //      this.url = url;
  //
  //      AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
  //      client = factory.createWithBasicHttpAuthentication(new URI(url), username, password);
  //
  //      // Do some execution
  //
  //      return new JiraResponse<User>(client.getUserClient().getUser(username).claim());
  //    } catch (URISyntaxException e) {
  //      return new JiraResponse("Error: " + e.getMessage());
  //    } catch (RestClientException e) {
  //      return new JiraResponse("Error: " + e.getCause().toString());
  //    } catch (IllegalArgumentException e) {
  //      return new JiraResponse("Error: " + e.getMessage());
  //    } finally {
  //      close();
  //    }
  //  });
  //}



  ///**
  // * Fetches all the worklogs for today
  // */
  //public void executeRequest(JiraObject object) {
  //  executeInBackground(() -> {
  //    try {
  //      AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
  //      client = factory.createWithBasicHttpAuthentication(new URI(url), username, password);
  //
  //      //SearchResult searchResult = client.getSearchClient().searchJql(WORKLOG_FOR_TODAY).claim();
  //      //for (Issue issueLink : searchResult.getIssues()) {
  //      //  Issue issue = client.getIssueClient().getIssue(issueLink.getKey()).claim();
  //      //  System.out.println("Issue: "+issue.getKey());
  //      //  Promise<List<Worklog>> worklogPromise = client.getIssueWorklogRestClient().getIssueWorklogs(
  //      //      issue);
  //      //  List<Worklog> workLogs = worklogPromise.claim();
  //      //  for (Worklog workLog : workLogs) {
  //      //    System.out.println("Worklog: "+workLog);
  //      //  }
  //      //}
  //      //return new JiraObject<SearchResult>(searchResult);
  //
  //    } catch (URISyntaxException e) {
  //      return new JiraObject("Error: "+e.getMessage());
  //    } catch (RestClientException e) {
  //      return new JiraObject("Error: "+e.getCause().toString());
  //    } catch (IllegalArgumentException e) {
  //      return new JiraObject("Error: "+e.getMessage());
  //    } finally {
  //      close();
  //    }
  //  });
  //}

  //endregion


}
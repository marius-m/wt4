package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import lt.markmerkk.jira.entities.JiraResponse;
import lt.markmerkk.jira.extend_base.AsynchronousJiraRestClientFactoryPlus;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IJiraResponse;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public abstract class JiraWorker {
  Credentials credentials;

  JiraRestClientPlus client;

  public JiraWorker(Credentials credentials) {
    this.credentials = credentials;
  }

  abstract IJiraResponse executeRequest(JiraRestClient client);

  public IJiraResponse execute() {
    if (!credentials.isUserValid()) return new JiraResponse("Error: Invalid user credentials!");
    try {
      AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
      client = factory.createWithBasicHttpAuthentication(new URI(credentials.getUrl()),
          credentials.getUsername(), credentials.getPassword());
      return executeRequest(client);
    } catch (URISyntaxException e) {
      return new JiraResponse("Error: " + e.getMessage());
    } catch (RestClientException e) {
      return new JiraResponse("Error: " + e.getCause().toString());
    } catch (IllegalArgumentException e) {
      return new JiraResponse("Error: " + e.getMessage());
    } finally {
      close();
    }
  }

  /**
   * Closes any jira connection
   */
  protected void close() {
    try {
      if (client != null)
        client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

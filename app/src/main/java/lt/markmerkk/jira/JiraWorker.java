package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import lt.markmerkk.jira.entities.ErrorResponse;
import lt.markmerkk.jira.extend_base.AsynchronousJiraRestClientFactoryPlus;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IWorker;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents worker extension class that connects to jira client and passes down execution
 * to {@link #executeRequest(JiraRestClient)}.
 */
public abstract class JiraWorker implements IWorker {
  Credentials credentials;

  JiraRestClientPlus client;

  public JiraWorker(Credentials credentials) {
    if (credentials == null)
      throw new IllegalArgumentException("Cannot function without credentials!");
    this.credentials = credentials;
  }

  abstract IResponse executeRequest(JiraRestClient client);

  public IResponse execute() {
    if (!credentials.isUserValid()) return new ErrorResponse(null, "Error: Invalid user credentials!");
    try {
      AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
      client = factory.createWithBasicHttpAuthentication(new URI(credentials.getUrl()),
          credentials.getUsername(), credentials.getPassword());
      return executeRequest(client);
    } catch (URISyntaxException e) {
      return new ErrorResponse(null, "Error: " + e.getMessage());
    } catch (RestClientException e) {
      return new ErrorResponse(null, "Error: " + e.getCause().toString());
    } catch (IllegalArgumentException e) {
      return new ErrorResponse(null, "Error: " + e.getMessage());
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

package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.entities.ErrorResponse;
import lt.markmerkk.jira.extend_base.AsynchronousJiraRestClientFactoryPlus;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IWorker;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents worker extension class that connects to jira client and passes down execution
 * to {@link #executeRequest(JiraRestClient)}.
 */
public abstract class JiraWorker<T> implements IWorker<T> {
  protected ICredentials credentials;
  protected JiraRestClientPlus client;

  public JiraWorker() {}

  @Override public void populateCredentials(ICredentials credentials) {
    this.credentials = credentials;
  }

  protected abstract IResponse executeRequest(JiraRestClient client);

  public IResponse execute() {
    if (credentials == null) return new ErrorResponse(tag(), "Error: No user credentials provided!");
    if (!credentials.isValid()) return new ErrorResponse(tag(), "Error: Invalid user credentials!");
    try {
      AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
      client = factory.createWithBasicHttpAuthentication(new URI(credentials.url()),
          credentials.username(), credentials.password());
      return executeRequest(client);
    } catch (URISyntaxException e) {
      return new ErrorResponse(tag(), "Error: " + e.getMessage());
    } catch (RestClientException e) {
      return new ErrorResponse(tag(), "Error: " + e.getCause().toString());
    } catch (IllegalArgumentException e) {
      return new ErrorResponse(tag(), "Error: " + e.getMessage());
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

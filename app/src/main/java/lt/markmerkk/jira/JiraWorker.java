package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.extend_base.AsynchronousJiraRestClientFactoryPlus;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.jira.interfaces.IWorker;

/**
 * Created by mariusmerkevicius on 11/26/15.
 * Represents worker extension class that connects to jira client and passes down execution
 * to {@link #executeRequest(JiraRestClientPlus)}.
 */
public abstract class JiraWorker implements IWorker {
  protected ICredentials credentials;
  protected JiraRestClientPlus client;

  public JiraWorker() {}

  @Override public void populateCredentials(ICredentials credentials) {
    this.credentials = credentials;
  }

  protected abstract IWorkerResult executeRequest(JiraRestClientPlus client);

  public IWorkerResult execute() {
    if (credentials == null) return new ErrorWorkerResult(tag(), "No user credentials provided!");
    if (!credentials.isValid()) return new ErrorWorkerResult(tag(), "Invalid user credentials!");
    try {
      AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
      client = factory.createWithBasicHttpAuthentication(new URI(credentials.url()),
          credentials.username(), credentials.password());
      return executeRequest(client);
    } catch (URISyntaxException e) {
      return new ErrorWorkerResult(tag(), "Error: " + e.getMessage());
    } catch (RestClientException e) {
      return new ErrorWorkerResult(tag(), "Error: " + e.getCause().toString());
    } catch (IllegalArgumentException e) {
      return new ErrorWorkerResult(tag(), "Error: " + e.getMessage());
    } finally {
      close();
    }
  }

  // Doing mandatory checks for all instances
  @Override public String postExecuteMessage(IWorkerResult result) {
    if (result == null) return "Error getting result!";
    if (result instanceof ErrorWorkerResult)
      return "Error: " + ((ErrorWorkerResult) result).getErrorMessage();
    return null;
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

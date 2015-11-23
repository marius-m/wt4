package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;

/**
 * Created by mariusmerkevicius on 11/19/15.
 * Controller to
 */
public class JiraController implements IRemote {

  Log log;
  private JiraRestClient restClient;

  public JiraController(Log log) {
    this.log = log;
  }

  @Override public boolean isConnectionValid(String url, String username, String password) {
    log.info("Testing connection to jira...");
    try {
      final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
      restClient = factory.createWithBasicHttpAuthentication(
          new URI(url), username, password);
      Promise<User> promiseUser = restClient.getUserClient().getUser(username);
      User user = promiseUser.claim();
      log.info("Jira works! "+user.toString());
      return true;
    } catch (URISyntaxException e) {
      log.info("Error connecting to jira! "+e.getMessage());
      return false;
    } catch (RestClientException e) {
      log.info("Error connecting to jira! "+e.getMessage());
      return false;
    } catch (IllegalArgumentException e) {
      log.info("Error connecting to jira! "+e.getMessage());
      return false;
    } finally {
      try {
        if (restClient != null)
          restClient.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override public void destroy() {
  }
}

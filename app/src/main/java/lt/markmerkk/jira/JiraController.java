package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;

/**
 * Created by mariusmerkevicius on 11/19/15.
 * Controller to
 */
public class JiraController implements IRemote {

  Log log;
  private JiraRestClient restClient;
  private final ExecutorService executorService;
  private Runnable userGetterRunnable;

  public JiraController(Log log) {
    this.log = log;
    executorService = Executors.newSingleThreadExecutor();
  }

  @Override public boolean isConnectionValid(final String url, final String username, final String password) {
    userGetterRunnable = new Runnable() {
      @Override public void run() {
        log.info("Testing connection to jira...");
        try {
          final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
          restClient = factory.createWithBasicHttpAuthentication(new URI(url), username, password);
          Promise<User> promiseUser = restClient.getUserClient().getUser(username);
          User user = promiseUser.claim();
          log.info("Jira works! " + user.toString());
        } catch (URISyntaxException e) {
          log.info("Error connecting to jira! " + e.getMessage());
        } catch (RestClientException e) {
          log.info("Error connecting to jira! " + e.getMessage());
        } catch (IllegalArgumentException e) {
          log.info("Error connecting to jira! " + e.getMessage());
        } finally {
          close();
        }
      }
    };
    executorService.execute(userGetterRunnable);
    return true;
  }

  @Override public void destroy() {
    log = null;
    close();
    executorService.shutdown();
  }

  /**
   * Closes any jira connection
   */
  private void close() {
    try {
      if (restClient != null)
        restClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

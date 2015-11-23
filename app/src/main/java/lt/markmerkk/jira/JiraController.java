package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mariusmerkevicius on 11/19/15.
 * Controller to
 */
public class JiraController implements IRemote {

  @Override public boolean isConnectionValid(String url, String username, String password) {
    try {
      final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
      final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
          new URI(url), username, password);
      User promiseUser = restClient.getUserClient().getUser("username").claim();
      return true;
    } catch (URISyntaxException e) {
      return false;
    } catch (RestClientException e) {
      return false;
    }
  }

  @Override public void start() {

  }

  @Override public void stop() {

  }
}

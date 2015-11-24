package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Session;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.MyPermissionsInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.testkit.client.jerseyclient.JerseyClientFactory;
import com.atlassian.util.concurrent.Promise;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/23/15.
 */
@Ignore // integration testings
public class JiraControllerTest {

  @Test public void testProperties() throws Exception {
    // Arrange
    // Act
    // Assert
    Properties user = new Properties();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("user.properties");
    user.load(inputStream);
    System.out.println(user.get("username"));
    System.out.println(user.get("password"));
  }

  @Test public void testSearch() throws Exception {
    // Arrange
    // Act
    // Assert
    final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
        new URI("https://jira.ito.lt"), "admin", "admin");
    SearchResult result = restClient.getSearchClient().searchJql("assignee = \"marius.m@ito.lt\" AND worklogDate >= \"2015/11/18\" AND worklogDate <= \"2015/11/19\"").claim();
    System.out.println(result);
  }

  @Test public void testSession() throws Exception {
    // Arrange
    // Act
    // Assert
    final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
    final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(new URI("https://jira.ito.lt"), "admin", "admin");
    Session session = restClient.getSessionClient().getCurrentSession().claim();
    System.out.println("Session: "+session);
  }

  @Test public void testNotAuthorized() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
      final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
          new URI("https://jira.ito.lt"), "admin", "admin");
      Promise<User> promiseUser = restClient.getUserClient().getUser("marius.m@ito.lt");
      promiseUser.claim();
      System.out.println("Done!");
    } catch (URISyntaxException e) {
      //e.printStackTrace();
    } catch (RestClientException e) {
      //e.printStackTrace();
      System.out.println(e.getCause());
    }
  }

}
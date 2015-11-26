package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.IResponse;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class JiraWorkerExecuteTest {
  @Test public void testValid() throws Exception {
    // Arrange
    MockWorker worker = spy(new MockWorker(new Credentials("asdf", "asdf", "asdf")));
    doReturn(mock(IResponse.class)).when(worker).executeRequest(any(JiraRestClient.class));

    // Act
    IResponse response = worker.execute();

    // Assert
    verify(worker).executeRequest(any(JiraRestClient.class));
    assertThat(response).isNotNull();
  }

  @Test public void testInvalidCredentials() throws Exception {
    // Arrange
    MockWorker worker = spy(new MockWorker(new Credentials(null, null, null)));
    //doReturn(mock(IJiraResponse.class)).when(worker).executeRequest(any(JiraRestClient.class));

    // Act
    IResponse response = worker.execute();

    // Assert
    verify(worker, never()).executeRequest(any(JiraRestClient.class));
    assertThat(response).isNotNull();
  }

  @Test public void testThrowErrors() throws Exception {
    // Arrange
    MockWorker worker = spy(new MockWorker(new Credentials("asdf", "asdf", "asdf")));
    doThrow(new RestClientException("Some problems", new Throwable("Some problems")))
        .when(worker).executeRequest(any(JiraRestClient.class));

    // Act
    IResponse response = worker.execute();

    // Assert
    verify(worker).executeRequest(any(JiraRestClient.class));
    assertThat(response).isNotNull();
  }

  //region Classes

  private class MockWorker extends JiraWorker {
    public MockWorker(Credentials credentials) {
      super(credentials);
    }

    @Override IResponse executeRequest(JiraRestClient client) {
      return null;
    }

    @Override public String tag() {
      return "some_valid_tag";
    }

    @Override public String preExecuteMessage() {
      return null;
    }
  }

  //endregion

}
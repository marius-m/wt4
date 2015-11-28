package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.jira.workers.JiraWorkerLogin;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class JiraWorkerPostExecuteTest {

  @Test public void testNullResult() throws Exception {
    // Arrange
    JiraWorker worker = new MockJiraWorker();

    // Act
    String message = worker.postExecuteMessage(null);

    // Assert
    assertThat(message).isEqualTo("Error getting result!");
  }

  @Test public void testResultFail() throws Exception {
    // Arrange
    JiraWorker worker = new MockJiraWorker();
    IWorkerResult result = new ErrorWorkerResult("some_tag", "error_message");

    // Act
    String message = worker.postExecuteMessage(result);

    // Assert
    assertThat(message).isEqualTo("Error: error_message");
  }

  private class MockJiraWorker extends JiraWorker {
    @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
      return null;
    }

    @Override public void populateInput(IWorkerResult result) {

    }

    @Override public String tag() {
      return null;
    }

    @Override public String preExecuteMessage() {
      return null;
    }
  }
}
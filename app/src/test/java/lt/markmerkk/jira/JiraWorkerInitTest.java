package lt.markmerkk.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import lt.markmerkk.jira.extend_base.JiraRestClientPlus;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/26/15.
 */
public class JiraWorkerInitTest {

  @Test public void testValid() throws Exception {
    // Arrange
    // Act
    // Assert
    assertThat(new MockWorker()).isNotNull();
  }

  //region Classes
  private class MockWorker extends JiraWorker {
    public MockWorker() { }

    @Override protected IWorkerResult executeRequest(JiraRestClientPlus client) {
      return null;
    }

    @Override public void populateInput(IWorkerResult result) { }

    @Override public String tag() {
      return null;
    }

    @Override public String preExecuteMessage() {
      return null;
    }

    @Override public String postExecuteMessage(IWorkerResult result) {
      return null;
    }
  }
  //endregion

}
package lt.markmerkk.jira.workers;

import lt.markmerkk.jira.workers.JiraWorkerLogin;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraWorkerLoginInitTest {
  @Test public void testValid() throws Exception {
    // Arrange
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin();

    // Act
    String tag = jiraWorkerLogin.tag();

    // Assert
    assertThat(tag).isNotNull();
  }
}
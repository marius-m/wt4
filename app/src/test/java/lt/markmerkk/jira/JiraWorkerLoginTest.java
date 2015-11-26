package lt.markmerkk.jira;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraWorkerLoginTest {
  @Test public void testValid() throws Exception {
    // Arrange
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin(new Credentials("asdf", "asfd", "asdf"));

    // Act
    // Assert
    assertThat(jiraWorkerLogin.tag()).isNotNull();
  }
}
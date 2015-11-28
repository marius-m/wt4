package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class JiraWorkerLoginPostExecuteTest {
  @Test public void testValid() throws Exception {
    // Arrange
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin();
    IWorkerResult result = mock(IWorkerResult.class);
    User user = mock(User.class);
    doReturn(true).when(result).isSuccess();
    doReturn(user).when(result).entity();
    doReturn("marius").when(user).getName();

    // Act
    String message = jiraWorkerLogin.postExecuteMessage(result);

    // Assert
    assertThat(message).isEqualTo("Success: User: marius");
  }

  @Test public void testNullResult() throws Exception {
    // Arrange
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin();
    IWorkerResult result = mock(IWorkerResult.class);
    User user = mock(User.class);
    doReturn(true).when(result).isSuccess();
    doReturn(user).when(result).entity();
    doReturn("marius").when(user).getName();

    // Act
    String message = jiraWorkerLogin.postExecuteMessage(null);

    // Assert
    assertThat(message).isEqualTo("Error getting result!");
  }

  @Test public void testNullUser() throws Exception {
    // Arrange
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin();
    IWorkerResult result = mock(IWorkerResult.class);
    doReturn(null).when(result).entity();
    doReturn(true).when(result).isSuccess();

    // Act
    String message = jiraWorkerLogin.postExecuteMessage(result);

    // Assert
    assertThat(message).isEqualTo("Internal error: Result of wrong type!");
  }

  @Test public void testResultFail() throws Exception {
    // Arrange
    JiraWorkerLogin jiraWorkerLogin = new JiraWorkerLogin();
    IWorkerResult result = new ErrorWorkerResult("some_tag", "error_message");

    // Act
    String message = jiraWorkerLogin.postExecuteMessage(result);

    // Assert
    assertThat(message).isEqualTo("Error getting login information! error_message");
  }

}
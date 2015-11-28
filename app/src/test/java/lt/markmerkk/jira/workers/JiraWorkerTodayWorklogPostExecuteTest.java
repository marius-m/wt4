package lt.markmerkk.jira.workers;

import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.User;
import lt.markmerkk.jira.entities.ErrorWorkerResult;
import lt.markmerkk.jira.entities.SuccessWorkerResult;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class JiraWorkerTodayWorklogPostExecuteTest {
  @Test public void testValid() throws Exception {
    // Arrange
    JiraWorkerTodayWorklog worker = new JiraWorkerTodayWorklog();
    SearchResult searchResult = mock(SearchResult.class);
    IWorkerResult result = new SuccessWorkerResult<>(worker.tag(), searchResult);

    // Act
    String message = worker.postExecuteMessage(result);

    // Assert
    assertThat(message).contains("Success: Worked on these issues: ");
  }

}
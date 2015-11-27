package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IScheduler;
import lt.markmerkk.jira.interfaces.JiraListener;
import lt.markmerkk.storage2.database.interfaces.IResult;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraExecutorOnResultTest {
  @Test public void testNull() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));

    // Act
    executor.onResult(null);

    // Assert
    verify(executor, never()).executeScheduler(any(IScheduler.class));
  }

  @Test public void testNullScheduler() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));

    // Act
    executor.onResult(mock(IResponse.class));

    // Assert
    verify(executor, never()).executeScheduler(any(IScheduler.class));
  }

  @Test public void testValid() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    IResponse response = mock(IResponse.class);
    executor.scheduler = mock(IScheduler.class);
    doReturn(true).when(response).isSuccess();
    doReturn(true).when(executor.scheduler).hasMore();
    doNothing().when(executor).executeScheduler(any(IScheduler.class));

    // Act
    executor.onResult(response);

    // Assert
    verify(executor.scheduler, times(1)).complete(any(IResponse.class));
    verify(executor, times(1)).executeScheduler(any(IScheduler.class));
  }
}
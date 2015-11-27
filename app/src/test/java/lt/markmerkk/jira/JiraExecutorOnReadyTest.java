package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IScheduler;
import lt.markmerkk.jira.interfaces.JiraListener;
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
public class JiraExecutorOnReadyTest {
  @Test public void testNullScheduler() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    executor.scheduler = null;
    //doReturn(true).when(executor.scheduler).hasMore();
    doNothing().when(executor).executeScheduler(any(IScheduler.class));

    // Act
    // Assert
    executor.onReady();
  }

  @Test public void testSchedulerDepleted() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    executor.scheduler = mock(IScheduler.class);
    doReturn(false).when(executor.scheduler).hasMore();
    doNothing().when(executor).executeScheduler(any(IScheduler.class));

    // Act
    executor.onReady();

    // Assert
    verify(executor, never()).executeScheduler(executor.scheduler);
  }

  @Test public void testValid() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    executor.scheduler = mock(IScheduler.class);
    doReturn(true).when(executor.scheduler).hasMore();
    doNothing().when(executor).executeScheduler(any(IScheduler.class));

    // Act
    executor.onReady();

    // Assert
    verify(executor).executeScheduler(executor.scheduler);
  }

}
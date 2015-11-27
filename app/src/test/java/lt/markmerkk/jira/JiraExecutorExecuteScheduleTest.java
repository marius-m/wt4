package lt.markmerkk.jira;

import java.util.concurrent.Callable;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.JiraListener;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraExecutorExecuteScheduleTest {
  @Test public void testNullScheduler() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));

    // Act
    executor.executeScheduler(null);

    // Assert
    verify(executor, never()).execute(any(IWorker.class));
  }

  @Test public void testNullEmptyScheduler() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));

    // Act
    executor.executeScheduler(new JiraScheduler("some_scheduler", mock(ICredentials.class)));

    // Assert
    verify(executor, never()).execute(any(IWorker.class));
  }

  @Test public void testValid() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));

    // Act
    executor.executeScheduler(new JiraScheduler("some_scheduler",
        mock(ICredentials.class),
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class)
    ));

    // Assert
    verify(executor).execute(any(IWorker.class));
  }

  @Test public void testValidFirstItem() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);

    // Act
    executor.executeScheduler(new JiraScheduler("some_scheduler",
        mock(ICredentials.class), worker1, worker2, worker3
    ));

    // Assert
    verify(executor).execute(worker1);
  }
}
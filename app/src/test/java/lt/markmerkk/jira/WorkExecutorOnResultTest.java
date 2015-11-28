package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import lt.markmerkk.jira.interfaces.WorkerListener;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkExecutorOnResultTest {
  @Test public void testNullResult() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(mock(WorkerListener.class)));

    // Act
    executor.onResult(null);

    // Assert
    verify(executor).executeScheduler(any(IScheduler2.class));
  }

  @Test public void testValidResult() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(mock(WorkerListener.class)));
    IWorkerResult result = mock(IWorkerResult.class);
    executor.scheduler = mock(IScheduler2.class);

    // Act
    executor.onResult(result);

    // Assert
    verify(executor).executeScheduler(any(IScheduler2.class));
    verify(executor.scheduler).handleResult(result);
  }
}
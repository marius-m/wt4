package lt.markmerkk.jira;

import java.util.concurrent.Callable;
import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.WorkerOutputListener;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkExecutorExecuteSchedulerTest {

  @Test public void testNull() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(null, mock(WorkerOutputListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));

    // Act
    executor.executeScheduler(null);

    // Assert
    verify(executor, never()).executeInBackground(any(Callable.class));
  }

  @Test public void testNotExecutable() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(null, mock(WorkerOutputListener.class)));
    IScheduler2 scheduler = mock(IScheduler2.class);
    doNothing().when(executor).executeInBackground(any(Callable.class));
    doReturn(false).when(scheduler).shouldExecute();

    // Act
    executor.executeScheduler(scheduler);

    // Assert
    verify(executor, never()).executeInBackground(any(Callable.class));
  }

  @Test public void testValid() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(null, mock(WorkerOutputListener.class)));
    IScheduler2 scheduler = mock(IScheduler2.class);
    doNothing().when(executor).executeInBackground(any(Callable.class));
    doReturn(true).when(scheduler).shouldExecute();
    doReturn(mock(IWorker.class)).when(scheduler).nextWorker();

    // Act
    executor.executeScheduler(scheduler);

    // Assert
    verify(executor).executeInBackground(any(Callable.class));
  }

}
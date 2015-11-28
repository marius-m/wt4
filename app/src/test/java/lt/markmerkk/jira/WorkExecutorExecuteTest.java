package lt.markmerkk.jira;

import java.util.concurrent.Callable;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.WorkerListener;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class WorkExecutorExecuteTest {
  @Test public void testNull() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(mock(WorkerListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));

    // Act
    executor.execute(null);

    // Assert
    verify(executor, never()).executeInBackground(any(Callable.class));
  }

  @Test public void testValid() throws Exception {
    // Arrange
    WorkExecutor executor = spy(new WorkExecutor(mock(WorkerListener.class)));
    doNothing().when(executor).executeInBackground(any(Callable.class));

    // Act
    executor.execute(mock(IWorker.class));

    // Assert
    verify(executor).executeInBackground(any(Callable.class));
  }

}
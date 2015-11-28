package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IScheduler2;
import lt.markmerkk.jira.interfaces.WorkerListener;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkExecutorCurrentSchedulerTest {
  @Test public void testNull() throws Exception {
    // Arrange
    WorkExecutor executor = new WorkExecutor(null, mock(WorkerListener.class));
    executor.scheduler = null;

    // Act
    IScheduler2 scheduler = executor.currentSchedulerOrEmptyOne();

    // Assert
    assertThat(scheduler).isNotNull();
  }

  @Test public void testValid() throws Exception {
    // Arrange
    WorkExecutor executor = new WorkExecutor(null, mock(WorkerListener.class));
    executor.scheduler = mock(IScheduler2.class);

    // Act
    IScheduler2 scheduler = executor.currentSchedulerOrEmptyOne();

    // Assert
    assertThat(scheduler).isNotNull();
    assertThat(scheduler).isEqualTo(executor.scheduler);
  }
}
package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkScheduler2HandleResultTest {
  @Test public void testValid() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    WorkScheduler2
        scheduler = spy(new WorkScheduler2(mock(ICredentials.class), worker1, worker2, worker3));
    doReturn("tag1").when(worker1).tag();
    doReturn("tag1").when(result).tag();
    doReturn(true).when(result).isSuccess();

    // Act
    scheduler.handleResult(result);

    // Assert
    assertThat(scheduler.workers.size()).isEqualTo(2);
  }

  @Test public void testNullResult() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    WorkScheduler2 scheduler = spy(
        new WorkScheduler2(mock(ICredentials.class), worker1, worker2, worker3));
    doReturn("tag1").when(worker1).tag();

    // Act
    scheduler.handleResult(null);

    // Assert
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }

  @Test public void testWrongTagResult() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    WorkScheduler2 scheduler = spy(
        new WorkScheduler2(mock(ICredentials.class), worker1, worker2, worker3));
    doReturn("tag1").when(worker1).tag();
    doReturn("tag3").when(result).tag();
    doReturn(true).when(result).isSuccess();

    // Act
    scheduler.handleResult(result);

    // Assert
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }

  @Test public void testResponseHasFailed() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    WorkScheduler2 scheduler = spy(
        new WorkScheduler2(mock(ICredentials.class), worker1, worker2, worker3));
    doReturn("tag1").when(worker1).tag();
    doReturn("tag1").when(result).tag();
    doReturn(false).when(result).isSuccess();

    // Act
    scheduler.handleResult(result);

    // Assert
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }

  @Test public void testResponseComeButNoWorkers() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    WorkScheduler2 scheduler = spy(
        new WorkScheduler2(mock(ICredentials.class)));
    doReturn("tag1").when(result).tag();
    doReturn(true).when(result).isSuccess();

    // Act
    scheduler.handleResult(result);

    // Assert
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }
}
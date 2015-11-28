package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkScheduler2ExecutionWorkerTest {
  @Test public void testValid() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorker worker4 = mock(IWorker.class);
    WorkScheduler2 scheduler = spy(new WorkScheduler2(
        mock(ICredentials.class), worker1, worker2, worker3,
        worker4
    ));
    doReturn(true).when(scheduler).shouldExecute();

    // Act
    IWorker worker = scheduler.nextWorker();

    // Assert
    assertThat(worker).isNotNull();
    assertThat(worker).isEqualTo(worker1);
  }

  @Test public void testShouldNotStart() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorker worker4 = mock(IWorker.class);
    WorkScheduler2 scheduler = spy(new WorkScheduler2(
        mock(ICredentials.class), worker1, worker2, worker3,
        worker4
    ));
    doReturn(false).when(scheduler).shouldExecute();

    // Act
    IWorker worker = scheduler.nextWorker();

    // Assert
    assertThat(worker).isNull();
  }

  // This cant happen
  @Test public void testNoJobsButShouldStart() throws Exception {
    // Arrange
    WorkScheduler2 scheduler = spy(
        new WorkScheduler2(mock(ICredentials.class)));
    doReturn(true).when(scheduler).shouldExecute();

    // Act
    IWorker worker = scheduler.nextWorker();

    // Assert
    assertThat(worker).isNull();
  }

  @Test public void testPickPriorityAfterRemoval() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorker worker4 = mock(IWorker.class);
    WorkScheduler2 scheduler = spy(new WorkScheduler2(
        mock(ICredentials.class), worker1, worker2, worker3,
        worker4
    ));
    doReturn(true).when(scheduler).shouldExecute();

    // Act
    IWorker worker = scheduler.nextWorker();

    // Assert
    assertThat(worker).isNotNull();
    assertThat(worker).isEqualTo(worker1);

    // Act
    scheduler.workers.remove(0);
    worker = scheduler.nextWorker();

    // Assert
    assertThat(worker).isNotNull();
    assertThat(worker).isEqualTo(worker2);
  }

  @Test public void testPopulateCredentials() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    WorkScheduler2 scheduler = spy(new WorkScheduler2(mock(ICredentials.class), worker1));
    doReturn(true).when(scheduler).shouldExecute();

    // Act
    IWorker worker = scheduler.nextWorker();

    // Assert
    verify(worker).populateCredentials(scheduler.credentials);
  }


}
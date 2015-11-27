package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class JiraScheduler2StartExecutionTest {
  @Test public void testValid() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorker worker4 = mock(IWorker.class);
    JiraScheduler2 scheduler = spy(new JiraScheduler2(
        mock(ICredentials.class), worker1, worker2, worker3,
        worker4
    ));
    doReturn(true).when(scheduler).shouldStartExecution();

    // Act
    IWorker worker = scheduler.startExecution();

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
    JiraScheduler2 scheduler = spy(new JiraScheduler2(
        mock(ICredentials.class), worker1, worker2, worker3,
        worker4
    ));
    doReturn(false).when(scheduler).shouldStartExecution();

    // Act
    IWorker worker = scheduler.startExecution();

    // Assert
    assertThat(worker).isNull();
  }

  // This cant happen
  @Test public void testNoJobsButShouldStart() throws Exception {
    // Arrange
    JiraScheduler2 scheduler = spy(
        new JiraScheduler2(mock(ICredentials.class)));
    doReturn(true).when(scheduler).shouldStartExecution();

    // Act
    IWorker worker = scheduler.startExecution();

    // Assert
    assertThat(worker).isNull();
  }

  @Test public void testPickPriorityAfterRemoval() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    IWorker worker2 = mock(IWorker.class);
    IWorker worker3 = mock(IWorker.class);
    IWorker worker4 = mock(IWorker.class);
    JiraScheduler2 scheduler = spy(new JiraScheduler2(
        mock(ICredentials.class), worker1, worker2, worker3,
        worker4
    ));
    doReturn(true).when(scheduler).shouldStartExecution();

    // Act
    IWorker worker = scheduler.startExecution();

    // Assert
    assertThat(worker).isNotNull();
    assertThat(worker).isEqualTo(worker1);

    // Act
    scheduler.workers.remove(0);
    worker = scheduler.startExecution();

    // Assert
    assertThat(worker).isNotNull();
    assertThat(worker).isEqualTo(worker2);
  }


}
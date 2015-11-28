package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkScheduler2ResetTest {
  @Test public void testValid() throws Exception {
    // Arrange
    WorkScheduler2 scheduler = new WorkScheduler2(
        mock(ICredentials.class),
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );

    // Act
    scheduler.reset();

    // Assert
    assertThat(scheduler.workers.size()).isZero();
  }

  @Test public void testNoWorkers() throws Exception {
    // Arrange
    WorkScheduler2 scheduler = new WorkScheduler2(
        mock(ICredentials.class)
    );

    // Act
    scheduler.reset();

    // Assert
    assertThat(scheduler.workers.size()).isZero();
  }
}
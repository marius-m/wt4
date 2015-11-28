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
public class WorkScheduler2InitTest {
  @Test public void testNull() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      WorkScheduler2 scheduler2 = new WorkScheduler2(null);
      fail("Should not create without credentials");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot init scheduler without credentials!");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    WorkScheduler2 scheduler2 = new WorkScheduler2(mock(ICredentials.class));

    // Act
    // Assert
    assertThat(scheduler2).isNotNull();
  }

  @Test public void testInitWithWorkers() throws Exception {
    // Arrange
    WorkScheduler2 scheduler = new WorkScheduler2(
        mock(ICredentials.class),
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );
    // Act
    // Assert
    assertThat(scheduler.workers).isNotNull();
    assertThat(scheduler.workers.size()).isEqualTo(4);
  }

  @Test public void testInitWithMalformedWorkers() throws Exception {
    // Arrange
    WorkScheduler2 scheduler = new WorkScheduler2(
        mock(ICredentials.class),
        null,
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );
    // Act
    // Assert
    assertThat(scheduler.workers).isNotNull();
    assertThat(scheduler.workers.size()).isEqualTo(3);
  }
}
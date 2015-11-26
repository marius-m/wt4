package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraSchedulerInitTest {
  @Test public void testInit1() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler();
    // Act
    // Assert
    assertThat(scheduler.workers()).isNotNull();
    assertThat(scheduler.workers().size()).isZero();
  }

  @Test public void testInitWithWorkers() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler(
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );
    // Act
    // Assert
    assertThat(scheduler.workers()).isNotNull();
    assertThat(scheduler.workers().size()).isEqualTo(4);
  }

  @Test public void testInitWithMalformedWorkers() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler(
        mock(IWorker.class),
        null,
        mock(IWorker.class),
        mock(IWorker.class)
    );
    // Act
    // Assert
    assertThat(scheduler.workers()).isNotNull();
    assertThat(scheduler.workers().size()).isEqualTo(3);
  }
}
package lt.markmerkk.jira;

import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class JiraScheduler2InitTest {
  @Test public void testNull() throws Exception {
    // Arrange
    // Act
    // Assert
    try {
      JiraScheduler2 scheduler2 = new JiraScheduler2(null);
      fail("Should not create without credentials");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot init scheduler without credentials!");
    }
  }

  @Test public void testValid() throws Exception {
    // Arrange
    JiraScheduler2 scheduler2 = new JiraScheduler2(mock(ICredentials.class));

    // Act
    // Assert
    assertThat(scheduler2).isNotNull();
  }

  @Test public void testInitWithWorkers() throws Exception {
    // Arrange
    JiraScheduler2 scheduler = new JiraScheduler2(
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
    JiraScheduler2 scheduler = new JiraScheduler2(
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
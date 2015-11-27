package lt.markmerkk.jira;

import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraSchedulerInitTest {
  @Test public void testInit1() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler(
        "some_name",
        new Credentials("asdf", "asdf", "asdf")
    );
    // Act
    // Assert
    assertThat(scheduler.workers()).isNotNull();
    assertThat(scheduler.workers().size()).isZero();
  }

  @Test public void testNoName() throws Exception {
    // Arrange
    try {
      JiraScheduler scheduler = new JiraScheduler(null,
          new Credentials("asdf", "asdf", "asdf"),
          mock(IWorker.class),
          mock(IWorker.class),
          mock(IWorker.class),
          mock(IWorker.class)
      );
      fail("should not create without a name");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot init scheduler without a name!");
    }
    // Act
    // Assert
  }
  @Test public void testNoCredentials() throws Exception {
    // Arrange
    try {
      JiraScheduler scheduler = new JiraScheduler("some_valid_name",
          null,
          mock(IWorker.class),
          mock(IWorker.class),
          mock(IWorker.class),
          mock(IWorker.class)
      );
      fail("should not create without credentials");
    } catch (Exception e) {
      assertThat(e).hasMessage("Cannot init scheduler without credentials!");
    }
    // Act
    // Assert
  }

  @Test public void testInitWithWorkers() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler("some_name",
        new Credentials("asdf", "asdf", "asdf"),
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
        "some_name",
        new Credentials("asdf", "asdf", "asdf"),
        null,
        mock(IWorker.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );
    // Act
    // Assert
    assertThat(scheduler.workers()).isNotNull();
    assertThat(scheduler.workers().size()).isEqualTo(3);
  }
}
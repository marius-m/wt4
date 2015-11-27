package lt.markmerkk.jira;

import lt.markmerkk.jira.entities.Credentials;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraSchedulerNextTest {
  @Test public void testReturnNextInLine() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    doReturn("TAG_1").when(worker1).tag();
    IWorker worker2 = mock(IWorker.class);
    doReturn("TAG_2").when(worker2).tag();
    IWorker worker3 = mock(IWorker.class);
    doReturn("TAG_3").when(worker3).tag();
    JiraScheduler scheduler = new JiraScheduler("some_name", new Credentials("asdf", "asdf", "asdf"), worker1,
        worker2,
        worker3
    );

    // Act
    // Assert
    assertThat(scheduler.next()).isEqualTo(worker1);
  }

  @Test public void testPopulateBeforeNext() throws Exception {
    // Arrange
    IWorker worker1 = mock(IWorker.class);
    doReturn("TAG_1").when(worker1).tag();
    IWorker worker2 = mock(IWorker.class);
    doReturn("TAG_2").when(worker2).tag();
    IWorker worker3 = mock(IWorker.class);
    doReturn("TAG_3").when(worker3).tag();
    JiraScheduler scheduler = new JiraScheduler("some_name", new Credentials("asdf", "asdf", "asdf"), worker1,
        worker2,
        worker3
    );

    // Act
    // Assert
    assertThat(scheduler.next()).isEqualTo(worker1);
    verify(worker1).populateCredentials(any(ICredentials.class));
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler("some_name", new Credentials("asdf", "asdf", "asdf"));

    // Act
    // Assert
    assertThat(scheduler.next()).isEqualTo(null);
  }

}
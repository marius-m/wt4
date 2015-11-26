package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
    JiraScheduler scheduler = new JiraScheduler(
        worker1,
        worker2,
        worker3
    );

    // Act
    // Assert
    assertThat(scheduler.next()).isEqualTo(worker1);
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler();

    // Act
    // Assert
    assertThat(scheduler.next()).isEqualTo(null);
  }

}
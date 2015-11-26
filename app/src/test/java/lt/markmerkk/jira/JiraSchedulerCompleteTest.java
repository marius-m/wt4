package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraSchedulerCompleteTest {
  @Test public void testValidComplete() throws Exception {
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
    IResponse response = mock(IResponse.class);
    doReturn("TAG_1").when(response).tag();

    // Act
    // Assert
    scheduler.complete(response);
    assertThat(scheduler.workers.size()).isEqualTo(2);
    assertThat(scheduler.workers.poll()).isEqualTo(worker2);
    assertThat(scheduler.workers.poll()).isEqualTo(worker3);
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }

  @Test public void testValidComplete2() throws Exception {
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
    IResponse response = mock(IResponse.class);
    doReturn("TAG_1")
        .doReturn("TAG_2")
        .doReturn("TAG_3")
        .when(response).tag();

    // Act
    // Assert
    scheduler.complete(response);
    assertThat(scheduler.workers.size()).isEqualTo(2);
    scheduler.complete(response);
    assertThat(scheduler.workers.size()).isEqualTo(1);
    scheduler.complete(response);
    assertThat(scheduler.workers.size()).isEqualTo(0);
    scheduler.complete(response);
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }

  @Test public void testTagMismatch() throws Exception {
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
    IResponse response = mock(IResponse.class);
    doReturn("TAG_3") // Scramble
        .doReturn("TAG_2")
        .doReturn("TAG_1")
        .when(response).tag();

    // Act
    // Assert
    try {
      scheduler.complete(response);
      fail("Should fail as tags do not beling to the sequence");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Error comparing IResponse and IWorker!");
    }
    assertThat(scheduler.workers.size()).isEqualTo(0);
  }

}
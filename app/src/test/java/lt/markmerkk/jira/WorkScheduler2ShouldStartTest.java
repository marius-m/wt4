package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkScheduler2ShouldStartTest {
  @Test public void testValid() throws Exception {
    // Arrange
    WorkScheduler2 scheduler2 = new WorkScheduler2(
        mock(ICredentials.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );

    // Act
    boolean shouldStart = scheduler2.shouldExecute(); // we have more jobs, we execute

    // Assert
    assertThat(shouldStart).isTrue();
  }

  @Test public void testNoJobs() throws Exception {
    // Arrange
    WorkScheduler2 scheduler2 = new WorkScheduler2(
        mock(ICredentials.class)
    );

    // Act
    boolean shouldStart = scheduler2.shouldExecute();

    // Assert
    assertThat(shouldStart).isFalse();
  }
}
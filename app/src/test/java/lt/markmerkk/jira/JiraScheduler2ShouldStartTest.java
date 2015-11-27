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
public class JiraScheduler2ShouldStartTest {
  @Test public void testValid() throws Exception {
    // Arrange
    JiraScheduler2 scheduler2 = new JiraScheduler2(
        mock(ICredentials.class),
        mock(IWorker.class),
        mock(IWorker.class)
    );

    // Act
    boolean shouldStart = scheduler2.shouldStartExecution(); // we have more jobs, we execute

    // Assert
    assertThat(shouldStart).isTrue();
  }

  @Test public void testNoJobs() throws Exception {
    // Arrange
    JiraScheduler2 scheduler2 = new JiraScheduler2(
        mock(ICredentials.class)
    );

    // Act
    boolean shouldStart = scheduler2.shouldStartExecution();

    // Assert
    assertThat(shouldStart).isFalse();
  }
}
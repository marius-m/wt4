package lt.markmerkk.jira;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraSchedulerIsCompleteTest {
  @Test public void testSize0() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler();
    // Act
    // Assert
    assertThat(scheduler.workers().size()).isZero();
    assertThat(scheduler.isComplete()).isTrue();
  }

}
package lt.markmerkk.jira;

import lt.markmerkk.jira.entities.Credentials;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraSchedulerIsCompleteTest {
  @Test public void testSize0() throws Exception {
    // Arrange
    JiraScheduler scheduler = new JiraScheduler("some_name", new Credentials("Asdf","adsf", "asdf"));
    // Act
    // Assert
    assertThat(scheduler.workers().size()).isZero();
  }

}
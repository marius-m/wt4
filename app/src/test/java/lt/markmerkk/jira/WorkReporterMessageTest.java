package lt.markmerkk.jira;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkReporterMessageTest {
  @Test public void testValid() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();

    // Act
    String message = reporter.reportMessage("valid_message");

    // Assert
    assertThat(message).isEqualTo("valid_message");
  }

  @Test public void testEmpty() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();

    // Act
    String message = reporter.reportMessage("");

    // Assert
    assertThat(message).isEqualTo("");
  }

  @Test public void testNull() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();

    // Act
    String message = reporter.reportMessage(null);

    // Assert
    assertThat(message).isEqualTo("");
  }
}
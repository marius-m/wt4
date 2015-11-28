package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IWorker;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkReporterStartTest {
  @Test public void testValid() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    doReturn("valid_message").when(worker).preExecuteMessage();

    // Act
    String message = reporter.reportWorkStart(worker);

    // Assert
    assertThat(message).isEqualTo("valid_message");
  }

  @Test public void testNullWorker() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();

    // Act
    String message = reporter.reportWorkStart(null);

    // Assert
    assertThat(message).isEqualTo("Error getting worker!");
  }

  @Test public void testWorkerNullPreexecute() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    doReturn(null).when(worker).preExecuteMessage();

    // Act
    String message = reporter.reportWorkStart(worker);

    // Assert
    assertThat(message).isEqualTo("");
  }

  @Test public void testWorkerEmptyPreexecute() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    doReturn("").when(worker).preExecuteMessage();

    // Act
    String message = reporter.reportWorkStart(worker);

    // Assert
    assertThat(message).isEqualTo("");
  }

  @Test public void testWorkerThrowOnPreexecute() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    doThrow(new ClassCastException("some_error")).when(worker).preExecuteMessage();

    // Act
    String message = reporter.reportWorkStart(worker);

    // Assert
    assertThat(message).isEqualTo("Error: some_error");
  }

}
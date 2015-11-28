package lt.markmerkk.jira;

import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.IWorkerResult;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/28/15.
 */
public class WorkReporterEndTest {
  @Test public void testValidSuccess() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    doReturn("valid_message").when(worker).postExecuteMessage(result);
    doReturn(true).when(result).isSuccess();

    // Act
    String message = reporter.reportWorkEnd(worker, result);

    // Assert
    assertThat(message).isEqualTo("valid_message");
  }

  @Test public void testNullResult() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    //IWorkerResult result = mock(IWorkerResult.class);
    //doReturn("valid_message").when(worker).postExecuteMessage(result);
    //doReturn(true).when(result).isSuccess();

    // Act
    String message = reporter.reportWorkEnd(worker, null);

    // Assert
    assertThat(message).isEqualTo("Error getting result!");
  }

  @Test public void testNullWorker() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    //IWorker worker = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    //doReturn("valid_message").when(worker).postExecuteMessage(result);
    doReturn(true).when(result).isSuccess();

    // Act
    String message = reporter.reportWorkEnd(null, result);

    // Assert
    assertThat(message).isEqualTo("Error getting worker!");
  }

  @Test public void testNullPostExecute() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    doReturn(null).when(worker).postExecuteMessage(result);
    doReturn(true).when(result).isSuccess();

    // Act
    String message = reporter.reportWorkEnd(worker, result);

    // Assert
    assertThat(message).isEqualTo("");
  }

  @Test public void testEmptyPostExecute() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    doReturn("").when(worker).postExecuteMessage(result);
    doReturn(true).when(result).isSuccess();

    // Act
    String message = reporter.reportWorkEnd(worker, result);

    // Assert
    assertThat(message).isEqualTo("");
  }

  @Test public void testExceptionOnPostExecute() throws Exception {
    // Arrange
    WorkReporter reporter = new WorkReporter();
    IWorker worker = mock(IWorker.class);
    IWorkerResult result = mock(IWorkerResult.class);
    doThrow(new ClassCastException("some_error")).when(worker).postExecuteMessage(result);
    doReturn(true).when(result).isSuccess();

    // Act
    String message = reporter.reportWorkEnd(worker, result);

    // Assert
    assertThat(message).isEqualTo("Error: some_error");
  }

}
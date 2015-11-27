package lt.markmerkk.jira;

import java.util.concurrent.Callable;
import lt.markmerkk.jira.interfaces.ICredentials;
import lt.markmerkk.jira.interfaces.IResponse;
import lt.markmerkk.jira.interfaces.IWorker;
import lt.markmerkk.jira.interfaces.JiraListener;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraExecutorPRODSchedulingTest {
  @Test public void testValid() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
    IResponse response = mock(IResponse.class);
    doReturn("tag1")
        .doReturn("tag2")
        .doReturn("tag3")
        .when(response).tag();
    doReturn(true)
        .doReturn(true)
        .doReturn(true)
        .when(response).isSuccess();
    doAnswer((InvocationOnMock invocationOnMock) -> {
      executor.onResult(response);
      executor.onReady();
      return null;
    }).when(executor).executeInBackground(any(Callable.class));

    IWorker worker1 = mock(IWorker.class);
    doReturn("tag1").when(worker1).tag();
    IWorker worker2 = mock(IWorker.class);
    doReturn("tag2").when(worker2).tag();
    IWorker worker3 = mock(IWorker.class);
    doReturn("tag3").when(worker3).tag();

    // Act
    executor.executeScheduler(
        new JiraScheduler("some_scheduler", mock(ICredentials.class), worker1, worker2, worker3
    ));

    // Assert
    verify(executor, times(3)).execute(any(IWorker.class));
  }

  @Test public void testValidWithError() throws Exception {
    // Arrange
    JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));

    IResponse response1 = mock(IResponse.class);
    doReturn("tag1").when(response1).tag();
    doReturn(false).when(response1).isSuccess();

    doAnswer((InvocationOnMock invocationOnMock) -> {
      executor.onResult(response1);
      return null;
    }).when(executor).executeInBackground(any(Callable.class));

    IWorker worker1 = mock(IWorker.class);
    doReturn("tag1").when(worker1).tag();
    IWorker worker2 = mock(IWorker.class);
    doReturn("tag2").when(worker2).tag();
    IWorker worker3 = mock(IWorker.class);
    doReturn("tag3").when(worker3).tag();

    // Act
    executor.executeScheduler(
        new JiraScheduler("some_scheduler", mock(ICredentials.class), worker1, worker2, worker3
    ));

    // Assert
    verify(executor, times(1)).execute(any(IWorker.class));
  }

}
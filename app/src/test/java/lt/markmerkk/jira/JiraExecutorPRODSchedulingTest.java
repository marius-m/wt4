package lt.markmerkk.jira;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/27/15.
 */
public class JiraExecutorPRODSchedulingTest {
  //@Test public void testValid() throws Exception {
  //  // Arrange
  //  JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
  //  IWorkerResponse response = mock(IWorkerResponse.class);
  //  doReturn("tag1")
  //      .doReturn("tag2")
  //      .doReturn("tag3")
  //      .when(response).tag();
  //  doReturn(true)
  //      .doReturn(true)
  //      .doReturn(true)
  //      .when(response).isSuccess();
  //  doAnswer((InvocationOnMock invocationOnMock) -> {
  //    executor.onResult(response);
  //    executor.onReady();
  //    return null;
  //  }).when(executor).executeInBackground(any(Callable.class));
  //
  //  IWorker worker1 = mock(IWorker.class);
  //  doReturn("tag1").when(worker1).tag();
  //  IWorker worker2 = mock(IWorker.class);
  //  doReturn("tag2").when(worker2).tag();
  //  IWorker worker3 = mock(IWorker.class);
  //  doReturn("tag3").when(worker3).tag();
  //
  //  // Act
  //  executor.executeScheduler(
  //      new JiraScheduler("some_scheduler", mock(ICredentials.class), worker1, worker2, worker3
  //  ));
  //
  //  // Assert
  //  verify(executor, times(3)).execute(any(IWorker.class));
  //}

  //@Test public void testValidWithError() throws Exception {
  //  // Arrange
  //  JiraExecutor executor = spy(new JiraExecutor(mock(JiraListener.class)));
  //
  //  IWorkerResponse response1 = mock(IWorkerResponse.class);
  //  doReturn("tag1").when(response1).tag();
  //  doReturn(false).when(response1).isSuccess();
  //
  //  doAnswer((InvocationOnMock invocationOnMock) -> {
  //    executor.onResult(response1);
  //    return null;
  //  }).when(executor).executeInBackground(any(Callable.class));
  //
  //  IWorker worker1 = mock(IWorker.class);
  //  doReturn("tag1").when(worker1).tag();
  //  IWorker worker2 = mock(IWorker.class);
  //  doReturn("tag2").when(worker2).tag();
  //  IWorker worker3 = mock(IWorker.class);
  //  doReturn("tag3").when(worker3).tag();
  //
  //  // Act
  //  executor.executeScheduler(
  //      new JiraScheduler("some_scheduler", mock(ICredentials.class), worker1, worker2, worker3
  //  ));
  //
  //  // Assert
  //  verify(executor, times(1)).execute(any(IWorker.class));
  //}

}
package lt.markmerkk.entities;

import lt.markmerkk.entities.database.interfaces.IExecutor;
import lt.markmerkk.entities.jobs.InsertJob;
import lt.markmerkk.entities.jobs.UpdateJob;
import net.rcarz.jiraclient.WorkLog;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/29/15.
 */
public class RemoteLogFetchMergerMergeTest {
  @Test public void testNew() throws Exception {
    // Arrange
    IExecutor executor = mock(IExecutor.class);
    String remoteIssue = "TT-123";
    WorkLog remoteWorklog = mock(WorkLog.class);

    RemoteLogFetchMerger merger = spy(new RemoteLogFetchMerger(executor));
    doReturn(null).when(merger).getLocalEntity(any(Long.class));
    doReturn(1234L).when(merger).getRemoteId(any(WorkLog.class));
    doReturn(new SimpleLog()).when(merger).newLog(anyString(), any(WorkLog.class));
    doReturn(new SimpleLog()).when(merger).updateLog(any(SimpleLog.class), anyString(),
        any(WorkLog.class));

    // Act
    merger.merge(remoteIssue, remoteWorklog);

    // Assert
    verify(executor).execute(any(InsertJob.class));
  }

  @Test public void testUpdate() throws Exception {
    // Arrange
    IExecutor executor = mock(IExecutor.class);
    String remoteIssue = "TT-123";
    WorkLog remoteWorklog = mock(WorkLog.class);

    RemoteLogFetchMerger merger = spy(
        new RemoteLogFetchMerger(executor));
    doReturn(mock(SimpleLog.class)).when(merger).getLocalEntity(any(Long.class));
    doReturn(1234L).when(merger).getRemoteId(any(WorkLog.class));
    doReturn(new SimpleLog()).when(merger).newLog(anyString(), any(WorkLog.class));
    doReturn(new SimpleLog()).when(merger).updateLog(any(SimpleLog.class), anyString(),
        any(WorkLog.class));

    // Act
    merger.merge(remoteIssue, remoteWorklog);

    // Assert
    verify(executor).execute(any(UpdateJob.class));
  }
}
package lt.markmerkk;

import com.atlassian.jira.rest.client.api.domain.Worklog;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
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
public class PullMergerMergeTest {
  @Test public void testNew() throws Exception {
    // Arrange
    IExecutor executor = mock(IExecutor.class);
    String remoteIssue = "TT-123";
    Worklog remoteWorklog = mock(Worklog.class);
    PullMerger merger = spy(
        new PullMerger(executor, remoteIssue, remoteWorklog));
    doReturn(null).when(merger).getLocalEntity(any(Long.class));
    doReturn(1234L).when(merger).getRemoteId(any(Worklog.class));
    doReturn(new SimpleLog()).when(merger).newLog(anyString(), any(Worklog.class));
    doReturn(new SimpleLog()).when(merger).updateLog(any(SimpleLog.class), anyString(),
        any(Worklog.class));

    // Act
    String output = merger.merge();

    // Assert
    assertThat(output).contains("Creating new log");
  }

  @Test public void testUpdate() throws Exception {
    // Arrange
    IExecutor executor = mock(IExecutor.class);
    String remoteIssue = "TT-123";
    Worklog remoteWorklog = mock(Worklog.class);
    PullMerger merger = spy(
        new PullMerger(executor, remoteIssue, remoteWorklog));
    doReturn(mock(SimpleLog.class)).when(merger).getLocalEntity(any(Long.class));
    doReturn(1234L).when(merger).getRemoteId(any(Worklog.class));
    doReturn(new SimpleLog()).when(merger).newLog(anyString(), any(Worklog.class));
    doReturn(new SimpleLog()).when(merger).updateLog(any(SimpleLog.class), anyString(),
        any(Worklog.class));

    // Act
    String output = merger.merge();

    // Assert
    assertThat(output).contains("Updating old worklog");
  }
}
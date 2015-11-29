package lt.markmerkk;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 11/29/15.
 */
public class PushNewMergerMergeTest {
  @Test public void testPullingInfinite() throws Exception {
    // Arrange
    PushNewMerger merger =
        spy(new PushNewMerger(mock(IExecutor.class), mock(JiraRestClient.class), DateTime.now()));
    doReturn(new SimpleLog()).when(merger).newRemoteLog(any(IExecutor.class));

    // Act
    merger.merge();

    // Assert
    verify(merger, times(50)).newRemoteLog(any(IExecutor.class));
  }

  @Test public void testPullingFinite() throws Exception {
    // Arrange
    PushNewMerger merger =
        spy(new PushNewMerger(mock(IExecutor.class), mock(JiraRestClient.class), DateTime.now()));
    doReturn(new SimpleLog())
        .doReturn(null)
        .when(merger).newRemoteLog(any(IExecutor.class));

    // Act
    merger.merge();

    // Assert
    verify(merger, times(2)).newRemoteLog(any(IExecutor.class));
  }

}
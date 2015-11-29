package lt.markmerkk;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.database.interfaces.IExecutor;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
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
    SimpleLog simpleLog = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-12")
        .setComment("Some comment")
        .build();
    PushNewMerger merger =
        spy(new PushNewMerger(mock(IExecutor.class), mock(JiraRestClient.class), DateTime.now()));
    doReturn(simpleLog).when(merger).newRemoteLog(any(IExecutor.class));
    doNothing().when(merger).updateLocalLog(any(SimpleLog.class));
    doReturn("Success!").when(merger).upload(any(SimpleLog.class));

    // Act
    merger.merge();

    // Assert
    verify(merger, times(50)).newRemoteLog(any(IExecutor.class));
  }

  @Test public void testPullingFinite() throws Exception {
    // Arrange
    SimpleLog simpleLog = new SimpleLogBuilder()
        .setStart(1000)
        .setEnd(2000)
        .setTask("TT-12")
        .setComment("Some comment")
        .build();
    PushNewMerger merger =
        spy(new PushNewMerger(mock(IExecutor.class), mock(JiraRestClient.class), DateTime.now()));
    doReturn(simpleLog)
        .doReturn(null)
        .when(merger).newRemoteLog(any(IExecutor.class));
    doNothing().when(merger).updateLocalLog(any(SimpleLog.class));
    doReturn("Success!").when(merger).upload(any(SimpleLog.class));

    // Act
    merger.merge();

    // Assert
    verify(merger, times(2)).newRemoteLog(any(IExecutor.class));
  }

}
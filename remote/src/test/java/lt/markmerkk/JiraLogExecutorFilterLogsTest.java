package lt.markmerkk;

import java.util.ArrayList;
import java.util.List;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 1/23/16.
 */
public class JiraLogExecutorFilterLogsTest {

  JiraLogExecutor executor;

  @Before
  public void setUp() {
      executor = new JiraLogExecutor();
  }

  @Test
  public void filterLogs_inputNullUser_shouldReturnEmpty() throws Exception {
    // Arrange
    // Act
    List<WorkLog> outLogs = executor.filterLogs(null, new DateTime(0), new DateTime(0), new ArrayList<WorkLog>() {{
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
    }});

    // Assert
    assertThat(outLogs).isNotNull();
    assertThat(outLogs.size()).isZero();
  }

  @Test
  public void filterLogs_inputNullStartDate_shouldReturnEmpty() throws Exception {
    // Arrange
    // Act
    List<WorkLog> outLogs = executor.filterLogs("asdf", null, new DateTime(0), new ArrayList<WorkLog>() {{
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
    }});

    // Assert
    assertThat(outLogs).isNotNull();
    assertThat(outLogs.size()).isZero();
  }

  @Test
  public void filterLogs_inputNullEndDate_shouldReturnEmpty() throws Exception {
    // Arrange
    // Act
    List<WorkLog> outLogs = executor.filterLogs("asdf", new DateTime(0), null, new ArrayList<WorkLog>() {{
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
    }});

    // Assert
    assertThat(outLogs).isNotNull();
    assertThat(outLogs.size()).isZero();
  }

  @Test
  public void filterLogs_nullLogList_shouldReturnEmpty() throws Exception {
    // Arrange
    // Act
    List<WorkLog> outLogs = executor.filterLogs("asdf", new DateTime(0), new DateTime(0), null);

    // Assert
    assertThat(outLogs).isNotNull();
    assertThat(outLogs.size()).isZero();
  }

  @Test
  public void filterLogs_emptyListList_shouldReturnEmpty() throws Exception {
    // Arrange
    // Act
    List<WorkLog> outLogs = executor.filterLogs("asdf", new DateTime(0), new DateTime(0), new ArrayList<WorkLog>() {{
    }});

    // Assert
    assertThat(outLogs).isNotNull();
    assertThat(outLogs.size()).isZero();
  }

  @Test
  public void filterLogs_valid_shouldReturn() throws Exception {
    // Arrange
    executor = spy(executor);
    doReturn(null).when(executor).filterLog(anyString(), any(DateTime.class), any(DateTime.class), any(WorkLog.class));

    // Act
    List<WorkLog> outLogs = executor.filterLogs("asdf", new DateTime(0), new DateTime(0), new ArrayList<WorkLog>() {{
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
      add(mock(WorkLog.class));
    }});

    // Assert
    verify(executor, times(3)).filterLog(anyString(), any(DateTime.class), any(DateTime.class), any(WorkLog.class));
  }

}
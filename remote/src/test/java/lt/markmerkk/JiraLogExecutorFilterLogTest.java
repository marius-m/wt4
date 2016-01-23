package lt.markmerkk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lt.markmerkk.interfaces.IRemoteListener;
import lt.markmerkk.interfaces.IRemoteLoadListener;
import net.rcarz.jiraclient.User;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 1/23/16.
 */
public class JiraLogExecutorFilterLogTest {

  JiraLogExecutor executor;
  private User user;
  private WorkLog worklog;

  @Before
  public void setUp() {
    executor = new JiraLogExecutor(mock(IRemoteListener.class), mock(IRemoteLoadListener.class));
    user = mock(User.class);
    worklog = mock(WorkLog.class);
    DateTime worklogTime = new DateTime(1000);
    doReturn(new Date(worklogTime.getMillis())).when(worklog).getStarted();
    doReturn(user).when(worklog).getAuthor();
    doReturn("asdf").when(user).getName();
  }

  @Test
  public void filterLog_valid_shouldReturnWorklog() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(500), new DateTime(2000), worklog);

    // Assert
    assertThat(log).isEqualTo(worklog);
  }

  @Test
  public void filterLog_endEqualCreate_shouldReturnWorklog() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(500), new DateTime(1000), worklog);

    // Assert
    assertThat(log).isEqualTo(worklog);
  }

  @Test
  public void filterLog_startEqualCreate_shouldReturnWorklog() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(1000), new DateTime(2000), worklog);

    // Assert
    assertThat(log).isEqualTo(worklog);
  }

  @Test
  public void filterLog_endBeforeCreate_shouldReturnWorklog() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(500), new DateTime(900), worklog);

    // Assert
    assertThat(log).isNull();
  }

  @Test
  public void filterLog_startAfterCreate_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(1200), new DateTime(2000), worklog);

    // Assert
    assertThat(log).isNull();
  }

  @Test
  public void filterLog_invalidUser_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("null", new DateTime(500), new DateTime(2000), worklog);

    // Assert
    assertThat(log).isNull();
  }

  @Test
  public void filterLog_nullUser_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog(null, new DateTime(500), new DateTime(2000), worklog);

    // Assert
    assertThat(log).isNull();
  }

  @Test
  public void filterLog_nullStartTime_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", null, new DateTime(2000), worklog);

    // Assert
    assertThat(log).isNull();
  }

  @Test
  public void filterLog_nullEndTime_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(500), null, worklog);

    // Assert
    assertThat(log).isNull();
  }

  @Test
  public void filterLog_nullWorkLog_shouldReturnNull() throws Exception {
    // Arrange
    // Act
    WorkLog log = executor.filterLog("asdf", new DateTime(500), new DateTime(2000), null);

    // Assert
    assertThat(log).isNull();
  }

}
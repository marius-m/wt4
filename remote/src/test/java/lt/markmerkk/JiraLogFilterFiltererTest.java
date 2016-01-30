package lt.markmerkk;

import java.util.Date;
import java.util.List;
import net.rcarz.jiraclient.User;
import net.rcarz.jiraclient.WorkLog;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 1/30/16.
 */
public class JiraLogFilterFiltererTest {
  private User user;
  private WorkLog worklog;
  private TestSubscriber<WorkLog> testSubscriber;

  @Before
  public void setUp() {
    testSubscriber = new TestSubscriber<>();
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
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(500), new DateTime(2000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    List<WorkLog> onNextEvents = testSubscriber.getOnNextEvents();
    assertThat(onNextEvents).isNotNull();
    assertThat(onNextEvents.size()).isEqualTo(1);
    assertThat(onNextEvents.get(0)).isEqualTo(worklog);
  }

  @Test
  public void filterLog_endEqualCreate_shouldReturnWorklog() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(500), new DateTime(1000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    List<WorkLog> onNextEvents = testSubscriber.getOnNextEvents();
    assertThat(onNextEvents).isNotNull();
    assertThat(onNextEvents.size()).isEqualTo(1);
    assertThat(onNextEvents.get(0)).isEqualTo(worklog);
  }

  @Test
  public void filterLog_startEqualCreate_shouldReturnWorklog() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(1000), new DateTime(2000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    List<WorkLog> onNextEvents = testSubscriber.getOnNextEvents();
    assertThat(onNextEvents).isNotNull();
    assertThat(onNextEvents.size()).isEqualTo(1);
    assertThat(onNextEvents.get(0)).isEqualTo(worklog);
  }

  @Test
  public void filterLog_endBeforeCreate_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(500), new DateTime(900), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }

  @Test
  public void filterLog_startAfterCreate_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(1200), new DateTime(2000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }

  @Test
  public void filterLog_invalidUser_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("null", new DateTime(500), new DateTime(2000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }

  @Test
  public void filterLog_nullUser_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer(null, new DateTime(500), new DateTime(2000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }

  @Test
  public void filterLog_nullStartTime_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", null, new DateTime(2000), worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }

  @Test
  public void filterLog_nullEndTime_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(500), null, worklog);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }

  @Test
  public void filterLog_nullWorkLog_shouldReturnNull() throws Exception {
    // Arrange
    JiraLogFilterer filterer = new JiraLogFilterer("asdf", new DateTime(500), new DateTime(2000), null);

    // Act
    Observable.create(filterer)
        .subscribe(testSubscriber);

    // Assert
    testSubscriber.assertError(JiraLogFilterer.FilterErrorException.class);
  }
}
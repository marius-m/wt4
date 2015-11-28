package lt.markmerkk.storage2;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import java.net.URI;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class SimpleLogBuilderTest {
  @Test
  public void testEmptyTime() throws Exception {
    SimpleLog log = new SimpleLogBuilder(1000).build();

    assertThat(log.getStart()).isEqualTo(1000);
    assertThat(log.getEnd()).isEqualTo(1000);
    assertThat(log.getDuration()).isEqualTo(0);
  }

  @Test
  public void testEmptyEnd() throws Exception {
    SimpleLog log = new SimpleLogBuilder(3000)
        .setStart(1000)
        .build();
    assertThat(log.getStart()).isEqualTo(1000);
    assertThat(log.getEnd()).isEqualTo(3000);
    assertThat(log.getDuration()).isEqualTo(2000);
  }

  @Test
  public void testEmptyStart() throws Exception {
    SimpleLog log = new SimpleLogBuilder(3000)
        .setEnd(6000)
        .build();
    assertThat(log.getStart()).isEqualTo(3000);
    assertThat(log.getEnd()).isEqualTo(6000);
    assertThat(log.getDuration()).isEqualTo(3000);
  }

  @Test
  public void testStartHigher() throws Exception {
    try {
      SimpleLog log = new SimpleLogBuilder(3000)
          .setStart(3000)
          .setEnd(1000)
          .build();
      fail("Should not create");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Invalid parameters!");
    }
  }

  @Test
  public void testComment() throws Exception {
    SimpleLog log = new SimpleLogBuilder(3000)
        .setComment("comment")
        .build();
    assertThat(log.getComment()).isEqualTo("comment");
  }

  @Test
  public void testTask() throws Exception {
    SimpleLog log = new SimpleLogBuilder(3000)
        .setTask("tt12")
        .build();
    assertThat(log.getTask()).isEqualTo("TT-12");
  }

  @Test
  public void testDuplicateLog() throws Exception {
    SimpleLogBuilder builder = new SimpleLogBuilder();

    SimpleLog oldLog = builder.setStart(1000).setEnd(2000).setTask("temp_task").setComment("temp_comment").build();
    oldLog.updateIndex(200);

    SimpleLog log = new SimpleLogBuilder(oldLog)
        .setTask("ttt123")
        .build();

    assertThat(log.getStart()).isEqualTo(1000); // old
    assertThat(log.getEnd()).isEqualTo(2000); // old
    assertThat(log.getDuration()).isEqualTo(1000); // old
    assertThat(log.getTask()).isEqualTo("TTT-123"); // new
    assertThat(log.getComment()).isEqualTo("temp_comment"); // old
    assertThat(log.get_id()).isEqualTo(200); // old
  }

  public final static DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  @Test
  public void testRemoteValid() throws Exception {
    //Assemble
    DateTime creationDate = longFormat.parseDateTime("2015-06-13 22:00");
    Worklog remoteLog = new Worklog(
        new URI("https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051"),
        new URI("http://not.needed.url"),
        mock(BasicUser.class),
        mock(BasicUser.class),
        "valid_comment",
        creationDate,
        creationDate,
        creationDate,
        20,
        Visibility.group("somegroup")
    );

    // Act
    SimpleLogBuilder builder = new SimpleLogBuilder("TT-22", remoteLog);
    SimpleLog log = builder.build();

    // Assert
    assertThat(log.getStart()).isEqualTo(creationDate.getMillis());
    assertThat(log.getEnd()).isEqualTo(new DateTime(creationDate).plusMinutes(20).getMillis());
    assertThat(log.getDuration()).isEqualTo(20 * 60 * 1000);
    assertThat(log.getTask()).isEqualTo("TT-22");
    assertThat(log.getComment()).isEqualTo("valid_comment");
    assertThat(log.get_id()).isEqualTo(0);
  }

}
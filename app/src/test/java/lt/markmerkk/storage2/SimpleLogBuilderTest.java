package lt.markmerkk.storage2;

import lt.markmerkk.storage2.entities.SimpleLog;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

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
        .setTask("task")
        .build();
    assertThat(log.getTask()).isEqualTo("task");
  }

  @Test
  public void testDuplicateLog() throws Exception {
    SimpleLog oldLog = new SimpleLog(1000, 2000, 1000, "temp_task", "temp_comment");
    oldLog.updateIndex(200);

    SimpleLog log = new SimpleLogBuilder(3000, oldLog)
        .setTask("task")
        .build();

    assertThat(log.getStart()).isEqualTo(1000); // old
    assertThat(log.getEnd()).isEqualTo(2000); // old
    assertThat(log.getDuration()).isEqualTo(1000); // old
    assertThat(log.getTask()).isEqualTo("task"); // new
    assertThat(log.getComment()).isEqualTo("temp_comment"); // old
    assertThat(log.get_id()).isEqualTo(200); // old
  }

}
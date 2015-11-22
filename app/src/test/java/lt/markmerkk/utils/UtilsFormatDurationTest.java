package lt.markmerkk.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mariusmerkevicius on 11/22/15.
 */
public class UtilsFormatDurationTest {
  @Before public void setUp() throws Exception {
  }

  @Test public void testEmpty() throws Exception {
    assertEquals("0s", Utils.formatDuration(0));
  }

  @Test public void testLowSecond() throws Exception {
    assertEquals("0s", Utils.formatDuration(60));
  }

  @Test public void testSeconds() throws Exception {
    long durationMillis = 1000;
    assertEquals("1s", Utils.formatDuration(durationMillis));
  }

  @Test public void testMinutes() throws Exception {
    long durationMillis = (60 * 1000);
    assertEquals("1m", Utils.formatDuration(durationMillis));
  }

  @Test public void testMinutesAndSeconds() throws Exception {
    long durationMillis = (60 * 1000) + 2000;
    assertEquals("1m 2s", Utils.formatDuration(durationMillis));
  }

  @Test public void testHours() throws Exception {
    long durationMillis = ((60 * 60 * 1000) + (10 * 60 * 1000) + 3000);
    assertEquals("1h 10m 3s", Utils.formatDuration(durationMillis));
  }

  @Test public void testDays() throws Exception {
    long durationMillis =
        ((2 * 24 * 60 * 60 * 1000) + (2 * 60 * 60 * 1000) + (20 * 60 * 1000) + 3000);
    assertEquals("50h 20m 3s", Utils.formatDuration(durationMillis));
  }
}
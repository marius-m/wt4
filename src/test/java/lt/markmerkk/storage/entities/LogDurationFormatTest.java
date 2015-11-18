package lt.markmerkk.storage.entities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LogDurationFormatTest {
    @Before
    public void setUp() throws Exception {}

    @Test
    public void testEmpty() throws Exception {
        assertEquals("0s", Log.formatDuration(0));
    }

    @Test
    public void testLowSecond() throws Exception {
        assertEquals("0s", Log.formatDuration(60));
    }

    @Test
    public void testSeconds() throws Exception {
        long durationMillis = 1000;
        assertEquals("1s", Log.formatDuration(durationMillis));
    }

    @Test
    public void testMinutes() throws Exception {
        long durationMillis = (60*1000);
        assertEquals("1m", Log.formatDuration(durationMillis));
    }

    @Test
    public void testMinutesAndSeconds() throws Exception {
        long durationMillis = (60*1000)+2000;
        assertEquals("1m 2s", Log.formatDuration(durationMillis));
    }

    @Test
    public void testHours() throws Exception {
        long durationMillis = ((60*60*1000) + (10*60*1000) + 3000);
        assertEquals("1h 10m 3s", Log.formatDuration(durationMillis));
    }

    @Test
    public void testDays() throws Exception {
        long durationMillis = ((2*24*60*60*1000) + (2*60*60*1000) + (20*60*1000) + 3000);
        assertEquals("50h 20m 3s", Log.formatDuration(durationMillis));
    }

}
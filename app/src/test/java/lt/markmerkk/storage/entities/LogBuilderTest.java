package lt.markmerkk.storage.entities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class LogBuilderTest {

//    private DateTime fromTime;
//    private DateTime toTime;
//    private DateTime nowTime;
    private DateTimeFormatter fullFormat;

    @Before
    public void setUp() throws Exception {
        fullFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeUtils.setCurrentMillisFixed(0);
    }

    @Test
    public void testEmptyTime() throws Exception {
        Log log = new Log.Builder().build();
        assertEquals(null, log.category);
        assertEquals(DateTimeUtils.currentTimeMillis(), log.start);
        assertEquals(DateTimeUtils.currentTimeMillis(), log.end);
        // Duration should be 0 as there should be no difference
        assertEquals(0, log.duration);
    }

    @Test
    public void testValidTime() throws Exception {
        DateTime fromTime = new DateTime(fullFormat.parseDateTime("2014-01-01 14:10:00"));
        DateTime toTime = new DateTime(fullFormat.parseDateTime("2014-01-01 15:23:00"));

        Log log = new Log.Builder()
                .setStart(fromTime.getMillis())
                .setEnd(toTime.getMillis())
                .build();
        assertEquals(fullFormat.parseDateTime("2014-01-01 14:10:00").getMillis(), log.start);
        assertEquals(fullFormat.parseDateTime("2014-01-01 15:23:00").getMillis(), log.end);
        assertEquals(new Duration(
                        fullFormat.parseDateTime("2014-01-01 14:10:00"),
                        fullFormat.parseDateTime("2014-01-01 15:23:00")
                ).getMillis(), log.duration);
    }

    @Test
    public void testEmptyEnd() throws Exception {
        DateTime fromTime = new DateTime(fullFormat.parseDateTime("2014-01-01 14:10:00"));
        // Mocking current time as this might throw an exception
        DateTimeUtils.setCurrentMillisFixed(
            fullFormat.parseDateTime("2014-01-01 15:30:00").getMillis());

        Log log = new Log.Builder()
                .setStart(fromTime.getMillis())
                .setMessage("New comment")
                .build();
        assertEquals(fullFormat.parseDateTime("2014-01-01 14:10:00").getMillis(), log.start);
        assertEquals(DateTimeUtils.currentTimeMillis(), log.end);
        assertEquals(new Duration(
                        fullFormat.parseDateTime("2014-01-01 14:10:00").getMillis(),
                        DateTimeUtils.currentTimeMillis()
                ).getMillis(), log.duration
        );
    }

    @Test
    public void testEmptyStart() throws Exception {
        DateTime toTime = new DateTime(fullFormat.parseDateTime("2014-01-01 15:23:00"));
        DateTime nowTime = new DateTime(DateTimeUtils.currentTimeMillis());
        System.out.println("Now is "+nowTime);
        Log log = new Log.Builder()
                .setEnd(toTime.getMillis())
                .build();
        assertEquals(DateTimeUtils.currentTimeMillis(), log.start);
        assertEquals(toTime.getMillis(), log.end);
        assertEquals(new Duration(
                        DateTimeUtils.currentTimeMillis(),
                        toTime.getMillis()
                ).getMillis(), log.duration
        );
    }

    @Test
    public void testFromHigherThanTo() throws Exception {
        DateTime toTime = new DateTime(fullFormat.parseDateTime("2014-01-01 15:23:00"));
        // Mocking current time to be later than end time
        DateTimeUtils.setCurrentMillisFixed(
            fullFormat.parseDateTime("2014-01-01 16:00:00").getMillis());
        try {
            Log log = new Log.Builder()
                    .setEnd(toTime.getMillis())
                    .build();
            assertFalse(true);
        } catch (Exception e) {
            assertEquals("\'start\' time cannot be higher than \'end\' time",
                    e.getMessage());
        }
    }

    @Test
    public void testEmptyMessage() throws Exception {
        Log log = new Log.Builder()
                .build();
        assertNull(log.comment);
        assertNull(log.git);
    }

    @Test
    public void testValidMessage() throws Exception {
        Log log = new Log.Builder()
                .setMessage("valid comment")
                .build();
        assertEquals("valid comment", log.comment);
    }
}
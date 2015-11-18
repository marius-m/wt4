package lt.markmerkk.utils;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class LoggerParseTimeTest {

    private Logger logger;
    private DateTimeFormatter fullLongFormat;

    @Before
    public void setUp() throws Exception {
        fullLongFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        logger = new Logger();
        DateTime nowTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        DateTimeUtils.setCurrentMillisFixed(nowTime.getMillis());
    }

    @Test
    public void testParsePartialValidTime() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        assertEquals(expectedTime, logger.parsePartialTime("14:10"));
    }

    @Test
    public void testParsePartialMalformedValidTime() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        assertEquals(expectedTime, logger.parsePartialTime("  14:10 "));
    }

    @Test
    public void testParsePartialMalformedInvalidTime() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        assertNotEquals(expectedTime, logger.parsePartialTime("  14a:10 "));
    }

    @Test
    public void testParsePartialNull() throws Exception {
        assertNull(logger.parsePartialTime(null));

    }

    @Test
    public void testParsePartialEmpty() throws Exception {
        assertNull(logger.parsePartialTime(""));

    }

    @Test
    public void testValidSeperatorFinder1() throws Exception {
        assertEquals("-", logger.findTimeSeparator("14:10-15:30"));
    }

    @Test
    public void testValidSeperatorFinder2() throws Exception {
        assertEquals("-", logger.findTimeSeparator("14:10 - 15:30"));
    }

    @Test
    public void testValidSeperatorFinder3() throws Exception {
        assertEquals(",", logger.findTimeSeparator("14:10,15:30"));
    }

    @Test
    public void testValidSeperatorFinder4() throws Exception {
        assertEquals("-", logger.findTimeSeparator("9:10-1:30"));
    }

    @Test
    public void testInvalidSeperatorFinder() throws Exception {
        // Misstype
        assertNull(logger.findTimeSeparator("14:10 until 15:30"));
    }

    @Test
    public void testCleanSeparators() throws Exception {
        assertEquals("14:10", logger.cleanSeparators("-/14:10,", Logger.VALID_TIME_SEPARATORS));
    }

    @Test
    public void testParseFullNullTime() throws Exception {
        ArrayList<DateTime> dateTimes = logger.parseFullTime(null);
        assertEquals(0, dateTimes.size());
    }

    @Test
    public void testParseFullValidTime() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:30:00"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10-15:30");
        assertEquals(2, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
        assertEquals(expectedTime2, dateTimes.get(1));
    }

    @Test
    public void testParseFullValidTime2() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:30:00"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10 , 15:30");
        assertEquals(2, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
        assertEquals(expectedTime2, dateTimes.get(1));
    }

    @Test
    public void testParseFullValidTime3() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 09:10:00"));
        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 05:30:00"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("9:10-5:30");
        assertEquals(2, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
        assertEquals(expectedTime2, dateTimes.get(1));
    }

    @Test
    public void testParsePartialValidTimeWithoutSeperator1() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 09:10:23"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("9:10:23");
        assertEquals(1, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
    }

    @Test
    public void testParsePartialValidTimeWithoutSeperator2() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 09:10:00"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("9:10");
        assertEquals(1, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
    }


    @Test
    public void testParseFullValidTimeWithSeconds() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:43"));
        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:30:52"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10:43 , 15:30:52");
        assertEquals(2, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
        assertEquals(expectedTime2, dateTimes.get(1));
    }

    @Test
    public void testParseFullInvalidTimeWithMalformedSeconds1() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:43"));
//        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:30:52"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10: ,");
        assertEquals(0, dateTimes.size());
    }

    @Test
    public void testParseFullInvalidTimeWithMalformedSeconds2() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:43"));
//        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:30:52"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10:43 , 15:23:1111");
        assertEquals(1, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
    }

    @Test
    public void testParseFullInvalidTime() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10-45:30");
        assertEquals(1, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
    }

    @Test
    public void testParseFullInvalidTimeMalformedSeparator() throws Exception {
        DateTime expectedTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        DateTime expectedTime2 = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:30:00"));
        ArrayList<DateTime> dateTimes = logger.parseFullTime("14:10 --, 15:30");
        assertEquals(2, dateTimes.size());
        assertEquals(expectedTime, dateTimes.get(0));
        assertEquals(expectedTime2, dateTimes.get(1));
    }
}
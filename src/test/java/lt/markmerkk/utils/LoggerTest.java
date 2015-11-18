package lt.markmerkk.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LoggerTest {

    private DateTimeFormatter fullLongFormat;
    private Logger logger;
    private DateTime nowTime;
    private MockLoggerParser mockListener;
    //    private Logger mockLogger;

    @Before
    public void setUp() throws Exception {
        fullLongFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        nowTime = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        logger = new Logger();
        mockListener = new MockLoggerParser();
        logger.setListener(mockListener);
        DateTimeUtils.setCurrentMillisFixed(nowTime.getMillis());
    }

    @Test
    public void testNull() throws Exception {
        assertFalse(logger.log(null));
    }


    @Test
    public void testNullMessage() throws Exception {
        assertFalse(logger.log(null));
    }

    @Test
    public void testEmptyMessage() throws Exception {
        assertTrue(logger.log(""));
        assertNull(mockListener.startTime);
        assertNull(mockListener.endTime);
        assertNull(mockListener.comment);
        assertNull(mockListener.taskTitle);

    }

    @Test
    public void testFull1() throws Exception {
        final DateTime expectedStart = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        final DateTime expectedEnd = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:43:00"));
        assertTrue(logger.log("14:10-15:43 > Komentaras >> TT-11"));
        assertEquals(expectedStart, mockListener.startTime);
        assertEquals(expectedEnd, mockListener.endTime);
        assertEquals("Komentaras", mockListener.comment);
        assertEquals("TT-11", mockListener.taskTitle);
    }

    @Test
    public void testFull2() throws Exception {
        final DateTime expectedStart = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:23"));
        final DateTime expectedEnd = new DateTime(fullLongFormat.parseDateTime("2014-01-01 15:43:52"));
        assertTrue(logger.log("14:10:23-15:43:52 >> TT-11 > Komentaras"));
        assertEquals(expectedStart, mockListener.startTime);
        assertEquals(expectedEnd, mockListener.endTime);
        assertEquals("Komentaras", mockListener.comment);
        assertEquals("TT-11", mockListener.taskTitle);
    }

    @Test
    public void testFull3() throws Exception {
        final DateTime expectedStart = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        assertTrue(logger.log("14:10 >> TT-11"));
        assertEquals(expectedStart, mockListener.startTime);
        assertNull(mockListener.endTime); // A bug! should return NOW
        assertNull(mockListener.comment);
        assertEquals("TT-11", mockListener.taskTitle);
    }

    @Test
    public void testFull4() throws Exception {
        assertTrue(logger.log(">Komentaras"));
        assertNull(mockListener.startTime);
        assertNull(mockListener.endTime); // A bug! should return NOW
        assertEquals("Komentaras", mockListener.comment);
        assertNull(mockListener.taskTitle);
    }

    @Test
    public void testFull5() throws Exception {
        final DateTime expectedStart = new DateTime(fullLongFormat.parseDateTime("2014-01-01 14:10:00"));
        assertTrue(logger.log("14:10 > Komentaras"));
        assertEquals(expectedStart, mockListener.startTime);
        assertNull(mockListener.endTime); // A bug! should return NOW
        assertEquals("Komentaras", mockListener.comment);
        assertNull(mockListener.taskTitle);
    }

}
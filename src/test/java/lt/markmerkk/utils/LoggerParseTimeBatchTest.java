package lt.markmerkk.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LoggerParseTimeBatchTest {

    private Logger logger;
    private DateTimeFormatter fullLongFormat;

    @Before
    public void setUp() throws Exception {
        fullLongFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        logger = new Logger();
    }

    @Test
    public void testNullCategory() throws Exception {
        assertNull(logger.pickTime(null));
    }

    @Test
    public void testEmptyCategory() throws Exception {
        assertNull(logger.pickTime(""));
    }

    @Test
    public void testFullTime1() throws Exception {
        assertEquals("14:10-15:43", logger.pickTime("14:10-15:43 > Komentaras >> TT-11"));
    }

    @Test
    public void testFullTime2() throws Exception {
        assertEquals("14:10-15:43", logger.pickTime("14:10-15:43 > Komentaras"));
    }

    @Test
    public void testFullTime3() throws Exception {
        assertEquals("14:10-15:43", logger.pickTime("14:10-15:43 >> TT-11"));
    }

    @Test
    public void testFullTime4() throws Exception {
        assertEquals("14:10:30-15:43:52", logger.pickTime("14:10:30-15:43:52 >> TT-11"));
    }

    @Test
    public void testFullTime5() throws Exception {
        assertEquals("14:10-15:43", logger.pickTime("14:10-15:43"));
    }

    @Test
    public void testFullTime6() throws Exception {
        assertEquals("14:10:30-15:43:52", logger.pickTime("14:10:30-15:43:52"));
    }

    @Test
    public void testPartialTime1() throws Exception {
        assertEquals("14:10", logger.pickTime("14:10"));
    }

    @Test
    public void testPartialMalformedTime1() throws Exception {
        assertEquals("14:10-", logger.pickTime("14:10-"));
    }

}
package lt.markmerkk.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LoggerParseCategoryTest {

    private Logger logger;
    private DateTimeFormatter fullLongFormat;

    @Before
    public void setUp() throws Exception {
        fullLongFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        logger = new Logger();
    }

    @Test
    public void testNullCategory() throws Exception {
        assertNull(logger.pickCategory(null));
    }

    @Test
    public void testEmptyCategory() throws Exception {
        assertNull(logger.pickCategory(""));
    }

    @Test
    public void testFullCategory1() throws Exception {
        assertEquals("TT-11", logger.pickCategory("14:10-15:43 > Komentaras >> TT-11"));
    }

    @Test
    public void testFullCategory2() throws Exception {
        assertEquals("TT-11", logger.pickCategory("14:10-15:43 >> TT-11 > Komentaras"));
    }

    @Test
    public void testFullCategory3() throws Exception {
        assertEquals("TT-11", logger.pickCategory("> Komentaras 14:10-15:43 >> TT-11"));
    }

    @Test
    public void testFullCategory4() throws Exception {
        assertNull(logger.pickCategory("> Komentaras"));
    }

    @Test
    public void testFullCategory5() throws Exception {
        assertEquals("TT-11", logger.pickCategory("> Komentaras >> TT-11"));
    }

    @Test
    public void testFullMalformed() throws Exception {
        assertEquals("TT-11", logger.pickCategory("14:10-15:43 >>> TT-11 >> Komentaras"));
    }

    @Test
    public void testFullBreakline1() throws Exception {
        assertEquals("TT-11", logger.pickCategory("14:10-15:43 >>> \nTT-11 >> Komentaras"));
    }

    @Test
    public void testFullBreakline2() throws Exception {
        // This kind of behavior is just plain user stupidness, cant validate that
        assertEquals("TT-11", logger.pickCategory("> Komentaras 14:10-15:43 >> TT-\n11"));
    }

    @Test
    public void testFullBreakline3() throws Exception {
        assertEquals("TT-11", logger.pickCategory("14:10-15:43 > Komentaras >> TT-11\n"));
    }

    @Test
    public void testFullBreakline4() throws Exception {
        assertEquals("TT-11", logger.pickCategory("14:10-15:43 >> TT-11\n > Komentaras "));
    }

}
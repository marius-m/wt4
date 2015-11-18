package lt.markmerkk.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LoggerParseMessageTest {

    private Logger logger;
    private DateTimeFormatter fullLongFormat;

    @Before
    public void setUp() throws Exception {
        fullLongFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        logger = new Logger();
    }

    @Test
    public void testNullComment() throws Exception {
        assertNull(logger.pickComment(null));
    }

    @Test
    public void testEmptyComment() throws Exception {
        assertNull(logger.pickComment(""));
    }

    @Test
    public void testFullComment1() throws Exception {
        assertEquals("Komentaras", logger.pickComment("14:10-15:43 > Komentaras >> TT-11"));
    }

    @Test
    public void testFullComment2() throws Exception {
        assertEquals("Komentaras", logger.pickComment("14:10-15:43 >> TT-11 > Komentaras"));
    }

    @Test
    public void testFullComment3() throws Exception {
        assertEquals("Komentaras 14:10-15:43", logger.pickComment("> Komentaras 14:10-15:43 >> TT-11"));
    }

    @Test
    public void testFullComment4() throws Exception {
        assertEquals("Komentaras", logger.pickComment("> Komentaras"));
    }


    // Line breaks should be avoided as all
    @Test
    public void testFullBreakline1() throws Exception {
        assertEquals("Komentaras Something else..", logger.pickComment("14:10-15:43 > Komentaras\n Something else.. >> TT-11"));
    }

    @Test
    public void testFullBreakline2() throws Exception {
        assertEquals("Komentaras", logger.pickComment("> Komentaras\n"));
    }

    @Test
    public void testFullBreakline3() throws Exception {
        // It should add comment after linebreak
        assertEquals("Komentaras", logger.pickComment("14:10-15:43 >> TT-11 > \nKomentaras"));
    }

    @Test
    public void testFullMalformedComment1() throws Exception {
        assertNull(logger.pickComment(">>> Komentaras"));
    }

    @Test
    public void testFullMalformedComment2() throws Exception {
        assertNull(logger.pickComment("14:10-15:43 >> Komentaras >> TT-11"));
    }
}
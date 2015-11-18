package lt.markmerkk.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void testValidEmpty1() throws Exception {
        assertTrue(Utils.isEmpty(""));
    }

    @Test
    public void testValidEmpty2() throws Exception {
        assertTrue(Utils.isEmpty(null));
    }

    @Test
    public void testInvalidEmpty() throws Exception {
        assertFalse(Utils.isEmpty("ascd"));
    }
}
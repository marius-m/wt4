package lt.markmerkk.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsInsertTaskSeperatorTest {

    @Test
    public void testEmpty() throws Exception {
        assertEquals("", Utils.insertTaskSeperator(""));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(Utils.insertTaskSeperator(null));
    }

    @Test
    public void testValid() throws Exception {
        assertEquals("tt-11", Utils.insertTaskSeperator("tt11"));
    }

    @Test
    public void testInvalid() throws Exception {
        assertEquals("11", Utils.insertTaskSeperator("11"));
    }
}
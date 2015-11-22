package lt.markmerkk.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsSplitTaskTitleTest {

    @Test
    public void testEmpty() throws Exception {
        assertEquals(null, Utils.splitTaskTitle(""));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(Utils.splitTaskTitle(null));
    }

    @Test
    public void testValid() throws Exception {
        assertEquals("TT", Utils.splitTaskTitle("TT12"));
    }

    @Test
    public void testValid2() throws Exception {
        assertEquals("TT", Utils.splitTaskTitle("TT-12"));
    }
    @Test
    public void testValid3() throws Exception {
        assertEquals("TT", Utils.splitTaskTitle("tt212"));
    }
    @Test
    public void testInvalid() throws Exception {
        assertNull(Utils.splitTaskTitle("212"));
    }

}
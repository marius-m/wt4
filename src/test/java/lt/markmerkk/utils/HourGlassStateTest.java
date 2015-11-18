package lt.markmerkk.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HourGlassStateTest {

    private HourGlass hourGlass;

    @Before
    public void setUp() throws Exception {
        hourGlass = new HourGlass();
    }

    @Test
    public void testStart() throws Exception {
        assertTrue(hourGlass.start());
        assertEquals(HourGlass.State.RUNNING, hourGlass.state);
    }

    @Test
    public void testStartWhenStarted() throws Exception {
        assertTrue(hourGlass.start());
        assertFalse(hourGlass.start());
        assertEquals(HourGlass.State.RUNNING, hourGlass.state);
    }

    @Test
    public void testStop() throws Exception {
        // should not destroy, as it is not running
        assertFalse(hourGlass.stop());
        assertEquals(HourGlass.State.STOPPED, hourGlass.state);
    }

    @Test
    public void testStopWhenStarted() throws Exception {
        assertTrue(hourGlass.start());
        assertTrue(hourGlass.stop());
        assertEquals(HourGlass.State.STOPPED, hourGlass.state);
    }

    @Test
    public void testRestart() throws Exception {
        assertTrue(hourGlass.start());
        assertTrue(hourGlass.restart());
        assertEquals(HourGlass.State.RUNNING, hourGlass.state);
    }

    @Test
    public void testRestartWhenNotRunning() throws Exception {
        assertTrue(hourGlass.restart());
        assertEquals(HourGlass.State.RUNNING, hourGlass.state);
    }
}
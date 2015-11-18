package lt.markmerkk.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaskControllerSplitTest {

    private TaskController taskController;

    @Before
    public void setUp() throws Exception {
        taskController = new TaskController(null);
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals(null, taskController.splitName(""));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(taskController.splitName(null));
    }

    @Test
    public void testValid() throws Exception {
        assertEquals("TT", taskController.splitName("TT12"));
    }

    @Test
    public void testValid2() throws Exception {
        assertEquals("TT", taskController.splitName("TT-12"));
    }
    @Test
    public void testValid3() throws Exception {
        assertEquals("TT", taskController.splitName("tt212"));
    }
    @Test
    public void testInvalid() throws Exception {
        assertNull(taskController.splitName("212"));
    }

}
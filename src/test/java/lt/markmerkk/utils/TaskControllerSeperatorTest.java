package lt.markmerkk.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaskControllerSeperatorTest {

    private TaskController taskController;

    @Before
    public void setUp() throws Exception {
        taskController = new TaskController(null);
    }


    @Test
    public void testEmpty() throws Exception {
        assertEquals("", taskController.insertMissingSeperator(""));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(taskController.insertMissingSeperator(null));
    }

    @Test
    public void testValid() throws Exception {
        assertEquals("tt-11", taskController.insertMissingSeperator("tt11"));
    }

    @Test
    public void testInvalid() throws Exception {
        assertEquals("11", taskController.insertMissingSeperator("11"));
    }
}
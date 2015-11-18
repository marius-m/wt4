package lt.markmerkk.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaskControllerTest {

    private TaskController taskController;

    @Before
    public void setUp() throws Exception {
        taskController = new TaskController(null);
    }

    @Test
    public void testEmpty() throws Exception {
        assertNull(taskController.inspectAndFormTitle(""));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(taskController.inspectAndFormTitle(null));
    }

    @Test
    public void testValid1() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("TT-11"));
    }

    @Test
    public void testValid2() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("TT11"));
    }

    @Test
    public void testValidLowerCase1() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("tt11"));
    }

    @Test
    public void testValidLowerCase2() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("tt-11"));
    }

    @Test
    public void testInvalidSpaces() throws Exception {
        assertNull(taskController.inspectAndFormTitle("TT - 11"));
    }

    @Test
    public void testValidMalformed1() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("at TT-11 asdc"));
    }

    @Test
    public void testValidMalformed2() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("at TT11 asdc"));
    }

    @Test
    public void testValidMalformed3() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("at tt11 asdc"));
    }

    @Test
    public void testValidLinebreak1() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("\ntt11\n"));
    }

    @Test
    public void testValidLinebreak2() throws Exception {
        assertEquals("TT-11", taskController.inspectAndFormTitle("\n tt11"));
    }

    // Missing seperator test

}
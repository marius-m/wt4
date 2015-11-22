package lt.markmerkk.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsValidateTaskTitleTest {

    @Test
    public void testEmpty() throws Exception {
        assertNull(Utils.validateTaskTitle(""));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(Utils.validateTaskTitle(null));
    }

    @Test
    public void testValid1() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("TT-11"));
    }

    @Test
    public void testValid2() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("TT11"));
    }

    @Test
    public void testValidLowerCase1() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("tt11"));
    }

    @Test
    public void testValidLowerCase2() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("tt-11"));
    }

    @Test
    public void testInvalidSpaces() throws Exception {
        assertNull(Utils.validateTaskTitle("TT - 11"));
    }

    @Test
    public void testValidMalformed1() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("at TT-11 asdc"));
    }

    @Test
    public void testValidMalformed2() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("at TT11 asdc"));
    }

    @Test
    public void testValidMalformed3() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("at tt11 asdc"));
    }

    @Test
    public void testValidLinebreak1() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("\ntt11\n"));
    }

    @Test
    public void testValidLinebreak2() throws Exception {
        assertEquals("TT-11", Utils.validateTaskTitle("\n tt11"));
    }

    // Missing seperator test

}
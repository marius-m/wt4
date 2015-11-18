package lt.markmerkk.storage.entities;

import java.util.ArrayList;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class TaskTest {

    @Test
    public void testValidGetTaskWithTitle() throws Exception {
        final Task mockTask = new Task("TT-11");
        assertEquals(mockTask, Task.getTaskWithTitle(new ArrayList<Task>(){{
            add(mockTask);
            add(new Task("TT-12"));
            add(new Task("TT-13"));
        }}, "TT-11"));
    }

    @Test
    public void testInvalidGetTaskWithTitle() throws Exception {
        assertNull("TT-11", Task.getTaskWithTitle(new ArrayList<Task>() {{
            add(new Task("TT-15"));
            add(new Task("TT-12"));
            add(new Task("TT-13"));
        }}, "TT-11"));
    }
}
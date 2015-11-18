package lt.markmerkk.storage.entities;

import java.util.ArrayList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProjectTest {

    @Test
    public void testValidGetProjectWithTitle() throws Exception {
        final Project mockProject = new Project("TT-11");
        assertEquals(mockProject, Project.getProjectWithTitle(new ArrayList<Project>(){{
            add(mockProject);
            add(new Project("TT-12"));
            add(new Project("TT-13"));
        }}, "TT-11"));
    }

    @Test
    public void testInvalidGetProjectWithTitle() throws Exception {
        assertNull("TT-11", Project.getProjectWithTitle(new ArrayList<Project>() {{
            add(new Project("TT-15"));
            add(new Project("TT-12"));
            add(new Project("TT-13"));
        }}, "TT-11"));
    }
}
package lt.markmerkk.storage.entities;

import java.util.ArrayList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LogFullCommentTest {

    @Test
    public void testOnlyComment() throws Exception {
        Log log = new Log();
        log.comment = "New comment";
        log.git = null;
        assertEquals(
                "Only comment should produce only comment message",
                "New comment",
                log.getFullComment()
        );
    }

    @Test
    public void testOnlyCommentEmptyGit() throws Exception {
        Log log = new Log();
        log.comment = "New comment";
        log.git = new ArrayList<String>();
        assertEquals(
                "Only comment and empty git log should produce only comment message",
                "New comment",
                log.getFullComment()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCommentEmptyGit() throws Exception {
        Log log = new Log();
        log.comment = null;
        log.git = new ArrayList<>();
        assertEquals(
                "Empty comment empty git should throw an illegal argument exception",
                null,
                log.getFullComment()
        );
    }
    @Test
    public void testOnlyGit() throws Exception {
        Log log = new Log();
        log.comment = null;
        log.git = new ArrayList<String>(){{
            add("Git message 1");
            add("Git message 2");
        }};
        assertEquals(
                "Empty comment empty git should throw an illegal argument exception",
                "GIT: Git message 1; Git message 2.",
                log.getFullComment()
        );
    }
    @Test
    public void testCommentAndGit() throws Exception {
        Log log = new Log();
        log.comment = "Some comment";
        log.git = new ArrayList<String>(){{
            add("Git message 1");
            add("Git message 2");
        }};
        assertEquals(
                "Empty comment empty git should throw an illegal argument exception",
                "Some comment (GIT: Git message 1; Git message 2.)",
                log.getFullComment()
        );
    }
}
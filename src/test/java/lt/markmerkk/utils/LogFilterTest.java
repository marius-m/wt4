package lt.markmerkk.utils;

import lt.mm.entities.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class LogFilterTest {

    private LogFilter logFilter;

    @Before
    public void setUp() throws Exception {
        logFilter = new LogFilter(new ArrayList<Log>(){{
            // Adding 3 remote logs
            for (int i = 0; i < 3; i++)
                add(new Log.RemoteBuilder()
                        .setCreated(System.currentTimeMillis())
                        .setServerUri("test_uri" + i)
                        .setCategory("TT-" + i)
                        .setStart(1000)
                        .setMinutesSpent(i)
                        .setMessage("Message" + i)
                        .build());
            // Adding 3 local logs
            for (int i = 0; i < 3; i++)
                add(new Log.Builder()
                        .setCategory("TT-"+i)
                        .setStart(1000)
                        .setEnd(1200)
                        .setMessage("Message"+i)
                        .build());
        }});
    }

    @Test
    public void testNull() throws Exception {
        logFilter.filterRemoteLogs(null);
        assertEquals("Null value should not filter anything",
                logFilter.missingRemoteLogs.size(), 0);
        assertEquals("Null value should not filter anything",
                logFilter.duplicateRemoteLogs.size(), 0);
    }

    @Test
    public void testEmpty() throws Exception {
        logFilter.filterRemoteLogs(new ArrayList<>());
        assertEquals("Null value should not filter anything",
                logFilter.missingRemoteLogs.size(), 0);
        assertEquals("Null value should not filter anything",
                logFilter.duplicateRemoteLogs.size(), 0);
    }

    @Test
    public void testSyncAddingNewLogs() throws Exception {
        logFilter.filterRemoteLogs(new ArrayList<Log>(){{
            // Remote log with unique server uri
            add(new Log.RemoteBuilder()
                    .setServerUri("test_uri4")
                    .setCategory("TT-1")
                    .setStart(1000)
                    .setMinutesSpent(4)
                    .setMessage("Message")
                    .build());
            add(new Log.RemoteBuilder()
                    .setServerUri("test_uri5")
                    .setCategory("TT-1")
                    .setStart(1000)
                    .setMinutesSpent(3)
                    .setMessage("Message")
                    .build());
        }});
        assertEquals("Two remote logs with unique server id should be added to missing remote log array",
                logFilter.missingRemoteLogs.size(), 2);
        assertEquals("Two remote logs with unique server id should not be added to duplicate array",
                logFilter.duplicateRemoteLogs.size(), 0);
    }

    @Test
    public void testSyncAddingNewUniqueAndDuplicate() throws Exception {
        logFilter.filterRemoteLogs(new ArrayList<Log>(){{
            // Adding a unique log
            add(new Log.RemoteBuilder()
                    .setServerUri("test_uri4")
                    .setCategory("TT-1")
                    .setStart(1000)
                    .setMinutesSpent(4)
                    .setMessage("Message")
                    .build());
            // Adding a duplicate log
            add(new Log.RemoteBuilder()
                    .setServerUri("test_uri1")
                    .setCategory("TT-1")
                    .setStart(1000)
                    .setMinutesSpent(3)
                    .setMessage("Message")
                    .build());
        }});
        assertEquals("Unique remote log should be added when uri does not match",
                logFilter.missingRemoteLogs.size(), 1);
        assertEquals("Remote log should be identified as duplicate when server uri matches",
                logFilter.duplicateRemoteLogs.size(), 1);
    }
}
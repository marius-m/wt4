package lt.markmerkk.utils

import org.joda.time.DateTime
import org.junit.Assert.*
import org.junit.Test
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class SyncController2SyncTest {
    @Test
    fun create_validCreation() {
        val controller = SyncController2()

        assertNotNull(controller)
    }

    @Test
    fun sync_validClient() {
        val controller = SyncController2()

        assertNull(controller.jiraClient)
        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        assertNotNull(controller.jiraClient)
    }

}
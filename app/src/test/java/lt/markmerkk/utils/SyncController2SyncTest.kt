package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.storage2.IDataStorage
import lt.markmerkk.storage2.SimpleLog
import lt.markmerkk.storage2.database.interfaces.IExecutor
import org.joda.time.DateTime
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers
import kotlin.test.assertNull

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class SyncController2SyncTest {
    val settings: UserSettings = mock()
    val dbExecutor: IExecutor = mock()
    val dbStorage: IDataStorage<SimpleLog> = mock()
    val lastUpdateController: LastUpdateController = mock()

    val controller = SyncController2(
            settings,
            dbExecutor,
            dbStorage,
            lastUpdateController
    )

    @Before
    fun setUp() {
        doReturn("test_host").whenever(settings).host
        doReturn("test_user").whenever(settings).username
        doReturn("test_pass").whenever(settings).password
    }

    @Test
    fun validSync_initJiraClient() {
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
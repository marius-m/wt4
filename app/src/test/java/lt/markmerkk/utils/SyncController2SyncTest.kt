package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.storage2.IDataStorage
import lt.markmerkk.storage2.SimpleLog
import lt.markmerkk.storage2.database.interfaces.IExecutor
import org.joda.time.DateTime
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers
import kotlin.test.assertEquals
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
    fun sync_triggerLoadings() {
        val remoteLoadingListener: IRemoteLoadListener = mock()
        controller.addLoadingListener(remoteLoadingListener)

        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        verify(remoteLoadingListener).onLoadChange(true)
        verify(remoteLoadingListener).onLoadChange(false)
    }

    @Test
    fun validClient_initJiraClient() {
        assertNull(controller.jiraClient)

        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        assertNotNull(controller.jiraClient)
    }

    @Test
    fun reuseJiraClient_multipleSync() {
        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )
        val newJiraClient = controller.jiraClient

        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )
        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        assertEquals(newJiraClient, controller.jiraClient)
    }

    @Test
    fun invalidClient_initJiraClient() {
        val remoteLoadingListener: IRemoteLoadListener = mock()
        controller.addLoadingListener(remoteLoadingListener)
        reset(settings) // will return null values which are invalid

        controller.sync(
                DateTime(1000),
                DateTime(2000),
                Schedulers.immediate(),
                Schedulers.immediate()
        )

        verify(remoteLoadingListener).onError(any())
        verify(remoteLoadingListener).onLoadChange(true)
        verify(remoteLoadingListener).onLoadChange(false)
    }

}
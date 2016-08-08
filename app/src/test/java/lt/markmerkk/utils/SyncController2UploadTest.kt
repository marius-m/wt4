package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import javafx.collections.ObservableList
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.JiraWork
import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.mvp.UserSettings
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class SyncController2UploadTest {
    val settings: UserSettings = mock()
    val lastUpdateController: LastUpdateController = mock()
    val dayProvider: DayProvider = mock()
    val jiraInteractor: JiraInteractor = mock()
    val remoteMergeToolsProvider: RemoteMergeToolsProvider = mock()
    val jiraClientProvider: JiraClientProvider = mock()
    val logStorage: BasicLogStorage = mock()

    val remoteLogPull: RemoteLogPull = mock()
    val fakeWork = JiraWork()

    val controller = SyncController2(
            jiraClientProvider = jiraClientProvider,
            jiraInteractor = jiraInteractor,
            logStorage = logStorage,
            userSettings = settings,
            remoteMergeToolsProvider = remoteMergeToolsProvider,
            lastUpdateController = lastUpdateController,
            dayProvider = dayProvider,
            uiScheduler = Schedulers.immediate(),
            ioScheduler = Schedulers.immediate()
    )

    @Before
    fun setUp() {
        doReturn("test_host").whenever(settings).host
        doReturn("test_user").whenever(settings).username
        doReturn("test_pass").whenever(settings).password

        doReturn(1000L).whenever(dayProvider).startDay()
        doReturn(2000L).whenever(dayProvider).endDay()

        doReturn(fakeWork).whenever(remoteLogPull).call()
        doReturn(remoteLogPull).whenever(remoteMergeToolsProvider).pullMerger(any(), any())
    }

    @Test
    fun emitItems_outputOnce() {
        // Arrange
        doReturn(listOf(
                SimpleLog(),
                SimpleLog(),
                SimpleLog()
        )).whenever(logStorage).dataAsList
        val testSubscriber = TestSubscriber<Any>()

        // Act
        controller.uploadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun noItems_outputOnce() {
        // Arrange
        doReturn(emptyList<SimpleLog>()).whenever(logStorage).dataAsList
        val testSubscriber = TestSubscriber<Any>()

        // Act
        controller.uploadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

}
package lt.markmerkk.interactors

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.*
import lt.markmerkk.entities.*
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.merger.RemoteLogPush
import lt.markmerkk.merger.RemoteMergeToolsProvider
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class SyncInteractorImplUploadTest {
    val settings: UserSettings = mock()
    val dayProvider: DayProvider = mock()
    val jiraInteractor: JiraInteractor = mock()
    val remoteMergeToolsProvider: RemoteMergeToolsProvider = mock()
    val logStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()
    val autoUpdateInteractor: AutoUpdateInteractor = mock()

    val remoteLogPull: RemoteLogPull = mock()
    val fakeWork = JiraWork()
    val validFilter: JiraFilter<SimpleLog> = mock()

    val controller = SyncInteractorImpl(
            jiraInteractor = jiraInteractor,
            logStorage = logStorage,
            userSettings = settings,
            remoteMergeToolsProvider = remoteMergeToolsProvider,
            dayProvider = dayProvider,
            uiScheduler = Schedulers.immediate(),
            ioScheduler = Schedulers.immediate(),
            autoUpdateInteractor = autoUpdateInteractor
    )

    @Before
    fun setUp() {
        doReturn("test_host").whenever(settings).host
        doReturn("test_user").whenever(settings).username
        doReturn("test_pass").whenever(settings).password

        doReturn(1000L).whenever(dayProvider).startDay()
        doReturn(2000L).whenever(dayProvider).endDay()

        doReturn(fakeWork).whenever(remoteLogPull).call()
        doReturn(remoteLogPull).whenever(remoteMergeToolsProvider).logPullMerger(any(), any())
        doReturn(true).whenever(validFilter).valid(any())
    }

    @Test
    fun emitItems_outputOnce() {
        // Arrange
        val remotePushMerge: RemoteLogPush = mock()
        doReturn(SimpleLog()).whenever(remotePushMerge).call()
        doReturn(remotePushMerge).whenever(remoteMergeToolsProvider).logPushMerger(any(), any())
        doReturn(Observable.just(
                listOf(
                        SimpleLog(),
                        SimpleLog(),
                        SimpleLog()
                )
        )).whenever(jiraInteractor).jiraLocalWorks()
        val testSubscriber = TestSubscriber<Any>()

        // Act
        controller.uploadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun valid_triggerMerge() {
        // Arrange
        doReturn(Observable.just(
                listOf(
                        SimpleLog(),
                        SimpleLog(),
                        SimpleLog()
                )
        )).whenever(jiraInteractor).jiraLocalWorks()
        val remotePushMerge: RemoteLogPush = mock()
        doReturn(SimpleLog()).whenever(remotePushMerge).call()
        doReturn(remotePushMerge).whenever(remoteMergeToolsProvider).logPushMerger(any(), any())
        val testSubscriber = TestSubscriber<Any>()

        // Act
        controller.uploadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        verify(remoteMergeToolsProvider, times(3)).logPushMerger(any(), any())
        verify(remotePushMerge, times(3)).call()
    }

    @Test
    fun noItems_outputOnce() {
        // Arrange
        doReturn(Observable.just(emptyList<SimpleLog>()))
                .whenever(jiraInteractor).jiraLocalWorks()
        val testSubscriber = TestSubscriber<Any>()

        // Act
        controller.uploadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

}
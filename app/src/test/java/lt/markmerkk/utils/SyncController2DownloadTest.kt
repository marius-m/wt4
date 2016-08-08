package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.JiraWork
import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.mvp.UserSettings
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-07
 */
class SyncController2DownloadTest {
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
    fun emptyResult_noTrigger() {
        reset(jiraInteractor)
        doReturn(Observable.empty<List<JiraWork>>()).whenever(jiraInteractor).jiraRemoteWorks(any(), any())

        controller.downloadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(TestSubscriber())

        verify(remoteMergeToolsProvider, never()).pullMerger(any(), any())
        verify(remoteLogPull, never()).call()
    }

    @Test
    fun validResult_triggerMerge() {
        reset(jiraInteractor)
        val validWorks = Observable.just(listOf(fakeWork))
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())

        controller.downloadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(TestSubscriber())

        verify(remoteMergeToolsProvider).pullMerger(any(), any())
        verify(remoteLogPull).call()
    }

    @Test
    fun validResult_triggerMerge_moreEntities() {
        reset(jiraInteractor)
        val validWorks = Observable.just(listOf(fakeWork, fakeWork, fakeWork, fakeWork))
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())

        controller.downloadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(TestSubscriber())

        verify(remoteMergeToolsProvider, times(4)).pullMerger(any(), any())
        verify(remoteLogPull, times(4)).call()
    }

    @Test
    fun noWorks_emitOutputOnce() {
        reset(jiraInteractor)
        val validWorks = Observable.just(emptyList<JiraWork>())
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())
        val testSubscriber = TestSubscriber<Any>()

        controller.downloadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        testSubscriber.assertValueCount(1)
    }

    @Test
    fun validResults_emitOutputOnce() {
        reset(jiraInteractor)
        val validWorks = Observable.just(listOf(fakeWork, fakeWork, fakeWork, fakeWork))
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())
        val testSubscriber = TestSubscriber<Any>()

        controller.downloadObservable()
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        testSubscriber.assertValueCount(1)
    }

}
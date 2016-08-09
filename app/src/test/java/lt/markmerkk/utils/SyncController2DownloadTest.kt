package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.BasicIssueStorage
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.mvp.UserSettings
import net.rcarz.jiraclient.WorkLog
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
    val logStorage: BasicLogStorage = mock()
    val issueStorage: BasicIssueStorage = mock()

    val remoteLogPull: RemoteLogPull = mock()
    val fakeWork = JiraWork()

    val validFilter: JiraFilter<WorkLog> = mock()

    val controller = SyncController2(
            jiraInteractor = jiraInteractor,
            logStorage = logStorage,
            issueStorage = issueStorage,
            userSettings = settings,
            remoteMergeToolsProvider = remoteMergeToolsProvider,
            lastUpdateController = lastUpdateController,
            dayProvider = dayProvider,
            uiScheduler = Schedulers.immediate()
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
    fun emptyResult_noTrigger() {
        reset(jiraInteractor)
        doReturn(Observable.empty<List<JiraWork>>()).whenever(jiraInteractor).jiraRemoteWorks(any(), any())

        controller.downloadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(TestSubscriber())

        verify(remoteMergeToolsProvider, never()).logPullMerger(any(), any())
        verify(remoteLogPull, never()).call()
    }

    @Test
    fun validResult_triggerMerge() {
        reset(jiraInteractor)
        val validWorks = Observable.just(listOf(fakeWork))
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())

        controller.downloadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(TestSubscriber())

        verify(remoteMergeToolsProvider).logPullMerger(any(), any())
        verify(remoteLogPull).call()
    }

    @Test
    fun validResult_triggerMerge_moreEntities() {
        reset(jiraInteractor)
        val validWorks = Observable.just(listOf(fakeWork, fakeWork, fakeWork, fakeWork))
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())

        controller.downloadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(TestSubscriber())

        verify(remoteMergeToolsProvider, times(4)).logPullMerger(any(), any())
        verify(remoteLogPull, times(4)).call()
    }

    @Test
    fun noWorks_emitOutputOnce() {
        reset(jiraInteractor)
        val validWorks = Observable.just(emptyList<JiraWork>())
        doReturn(validWorks).whenever(jiraInteractor).jiraRemoteWorks(any(), any())
        val testSubscriber = TestSubscriber<Any>()

        controller.downloadObservable(validFilter)
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

        controller.downloadObservable(validFilter)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        testSubscriber.assertValueCount(1)
    }

}
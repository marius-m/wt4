package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.JiraWork
import lt.markmerkk.mvp.UserSettings
import lt.markmerkk.storage2.BasicLogStorage
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-07
 */
class SyncController2FetchMergeTest {
    val settings: UserSettings = mock()
    val lastUpdateController: LastUpdateController = mock()
    val dayProvider: DayProvider = mock()
    val jiraInteractor: JiraInteractor = mock()
    val remoteMergeToolsProvider: RemoteMergeToolsProvider = mock()
    val jiraClientProvider: JiraClientProvider = mock()
    val logStorage: BasicLogStorage = mock()

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
    }

    @Test
    fun emptyResult_noTrigger() {
        reset(jiraInteractor)
        val validWorks = Observable.empty<List<JiraWork>>()
        doReturn(validWorks).whenever(jiraInteractor).jiraWorks(any(), any())

        controller.sync()

        verify(remoteMergeToolsProvider, never()).fetchMerger(any(), any())
    }

    @Test
    fun validResuult_triggerMerge() {
        reset(jiraInteractor)
        val fakeWork = JiraWork()
        val validWorks = Observable.just(listOf(fakeWork))
        doReturn(validWorks).whenever(jiraInteractor).jiraWorks(any(), any())

        controller.sync()

        verify(remoteMergeToolsProvider).fetchMerger(any(), any())
    }

}
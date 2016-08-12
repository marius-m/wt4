package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.IDataStorage
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.merger.RemoteLogPullImpl
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.*
import lt.markmerkk.merger.RemoteMergeToolsProvider
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class SyncController2SyncTest {
    val settings: UserSettings = mock()
    val lastUpdateController: LastUpdateController = mock()
    val dayProvider: DayProvider = mock()
    val jiraInteractor: JiraInteractor = mock()
    val remoteMergeToolsProvider: RemoteMergeToolsProvider = mock()
    val logStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()

    val controller = SyncInteractorImpl(
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
        doReturn(Observable.empty<List<JiraWork>>()).whenever(jiraInteractor).jiraRemoteWorks(any(), any())
        doReturn(Observable.empty<List<SimpleLog>>()).whenever(jiraInteractor).jiraLocalWorks()

        doReturn("test_host").whenever(settings).host
        doReturn("test_user").whenever(settings).username
        doReturn("test_pass").whenever(settings).password

        doReturn(1000L).whenever(dayProvider).startDay()
        doReturn(2000L).whenever(dayProvider).endDay()
    }

    @Test
    fun sync_triggerLoadings() {
        val remoteLoadingListener: IRemoteLoadListener = mock()
        controller.addLoadingListener(remoteLoadingListener)

        controller.syncLogs()

        verify(remoteLoadingListener).onLoadChange(true)
        verify(remoteLoadingListener).onLoadChange(false)
    }

}
package lt.markmerkk.interactors

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.DayProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteIssuePull
import lt.markmerkk.merger.RemoteLogPull
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.IDataStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.SyncInteractorImpl
import net.rcarz.jiraclient.Issue
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class SyncInteractorImplAllTest {

    val jiraInteractor: JiraInteractor = mock()
    val userSettings: UserSettings = mock()
    val logStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()
    val remoteToolsProvider: RemoteMergeToolsProvider = mock()
    val remoteIssueMerge: RemoteIssuePull = mock()
    val dayProvider: DayProvider = mock()
    val autoUpdateInteractor: AutoUpdateInteractor = mock()

    val sync = SyncInteractorImpl(
            jiraInteractor = jiraInteractor,
            userSettings = userSettings,
            logStorage = logStorage,
            issueStorage = issueStorage,
            remoteMergeToolsProvider = remoteToolsProvider,
            dayProvider = dayProvider,
            uiScheduler = Schedulers.immediate(),
            autoUpdateInteractor = autoUpdateInteractor
    )


    @Before
    fun setUp() {
        val validIssue: Issue = mock()
        whenever(jiraInteractor.jiraIssues())
                .thenReturn(Observable.empty())
        whenever(jiraInteractor.jiraLocalWorks())
                .thenReturn(Observable.empty())
        whenever(jiraInteractor.jiraRemoteWorks(any(), any()))
                .thenReturn(Observable.empty())
        whenever(jiraInteractor.jiraLocalIssuesOld(any()))
                .thenReturn(Observable.empty())

        doReturn("test_host").whenever(userSettings).host
        doReturn("test_user").whenever(userSettings).username
        doReturn("test_pass").whenever(userSettings).password

        doReturn(1000L).whenever(dayProvider).startDay()
        doReturn(2000L).whenever(dayProvider).endDay()

    }

    @Test
    fun valid_triggerDataChange() {
        // Assemble
        // Act
        sync.syncAll()

        // Assert
        verify(logStorage).notifyDataChange()
        verify(issueStorage).notifyDataChange()
    }

    @Test
    fun error_triggerDataChange() {
        // Assemble
        // Act
        sync.syncAll()

        // Assert
        verify(logStorage).notifyDataChange()
        verify(issueStorage).notifyDataChange()
    }
}
package lt.markmerkk.interactors

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.*
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteIssuePull
import lt.markmerkk.merger.RemoteMergeToolsProvider
import net.rcarz.jiraclient.Issue
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class SyncInteractorImplIssueTest {

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
            ioScheduler = Schedulers.immediate(),
            autoUpdateInteractor = autoUpdateInteractor
    )


    @Before
    fun setUp() {
        val validIssue: Issue = mock()
        whenever(jiraInteractor.jiraIssues())
                .thenReturn(Observable.just(
                        listOf(validIssue, validIssue, validIssue)
                ))
        whenever(remoteIssueMerge.call()).thenReturn(validIssue)
        whenever(remoteToolsProvider.issuePullMerger(any(), any(), any()))
                .thenReturn(remoteIssueMerge)
//        val newIssue: LocalIssue = mock()
//        whenever(newIssue.download_millis).thenReturn(1100)
        val issueForRemoval: LocalIssue = mock()
        whenever(issueForRemoval.download_millis).thenReturn(900)
        whenever(jiraInteractor.jiraLocalIssuesOld(any()))
                .thenReturn(Observable.just(listOf(issueForRemoval, issueForRemoval, issueForRemoval, issueForRemoval)))
    }

    @Test
    fun valid_triggerMerge() {
        // Assemble
        // Act
        sync.syncIssues()

        // Assert
        verify(remoteToolsProvider, times(3)).issuePullMerger(any(), any(), any())
        verify(remoteIssueMerge, times(3)).call()
    }

    @Test
    fun valid_emitOnce() {
        // Assemble
        val filter: JiraFilter<Issue> = mock()
        val testSubscriber = TestSubscriber<Any>()

        // Act
        sync.issueCacheObservable(filter, 1000L)
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun valid_triggerDataChange() {
        // Assemble

        // Act
        sync.syncIssues()

        // Assert
        verify(issueStorage).notifyDataChange()
    }

    @Test
    fun error_triggerDataChange() {
        // Assemble
        reset(jiraInteractor)
        whenever(jiraInteractor.jiraLocalIssuesOld(any())).thenReturn(Observable.empty())
        whenever(jiraInteractor.jiraIssues())
                .thenReturn(Observable.error<List<Issue>>(IllegalStateException("error_getting_issue")))

        // Act
        sync.syncIssues()

        // Assert
        verify(issueStorage).notifyDataChange()
    }

//    @Test
//    fun validWithOldItems_triggerRemoveOnOld() {
//        // Assemble
//
//        val filter: JiraFilter<Issue> = mock()
//        val testSubscriber = TestSubscriber<Any>()
//        val syncStart = 1000L
//
//        // Act
//        sync.issueCacheObservable(filter, syncStart)
//                .subscribeOn(Schedulers.immediate())
//                .observeOn(Schedulers.immediate())
//                .subscribe(testSubscriber)
//
//        // Assert
//        verify(issueStorage, times(4)).delete(any())
//    }


}
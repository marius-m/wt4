package lt.markmerkk.interactors

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.DayProvider
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.merger.RemoteIssuePull
import lt.markmerkk.merger.RemoteMergeToolsProvider
import lt.markmerkk.IDataStorage
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.SyncInteractorImpl
import net.rcarz.jiraclient.Issue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.Subscribers
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-11
 */
class SyncInteractorImplClearOldIssueCacheTest {

    val jiraInteractor: JiraInteractor = mock()
    val userSettings: UserSettings = mock()
    val logStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()
    val remoteToolsProvider: RemoteMergeToolsProvider = mock()
    val dayProvider: DayProvider = mock()

    val sync = SyncInteractorImpl(
            jiraInteractor = jiraInteractor,
            userSettings = userSettings,
            logStorage = logStorage,
            issueStorage = issueStorage,
            remoteMergeToolsProvider = remoteToolsProvider,
            dayProvider = dayProvider,
            uiScheduler = Schedulers.immediate()
    )


    @Before
    fun setUp() {
        val issueForRemoval: LocalIssue = mock()
        whenever(jiraInteractor.jiraLocalIssuesOld(any()))
                .thenReturn(Observable.just(listOf(
                        issueForRemoval,
                        issueForRemoval,
                        issueForRemoval,
                        issueForRemoval))
                )
    }

    @Test
    fun valid_triggerDelete() {
        // Assemble
        val testSubscriber = TestSubscriber<List<LocalIssue>>()

        // Act
        sync.clearOldIssueCacheObservable(1000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        verify(issueStorage, times(4)).delete(any())
    }

    @Test
    fun valid_emitOnce() {
        // Assemble
        val testSubscriber = TestSubscriber<List<LocalIssue>>()

        // Act
        sync.clearOldIssueCacheObservable(1000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun noItems_emitOnce() {
        // Assemble
        reset(jiraInteractor)
        whenever(jiraInteractor.jiraLocalIssuesOld(any()))
                .thenReturn(Observable.empty())
        val testSubscriber = TestSubscriber<List<LocalIssue>>()

        // Act
        sync.clearOldIssueCacheObservable(1000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun valid_emitDeletedItems() {
        // Assemble
        val testSubscriber = TestSubscriber<List<LocalIssue>>()

        // Act
        sync.clearOldIssueCacheObservable(1000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        assertEquals(4, testSubscriber.onNextEvents[0].size)
    }
}
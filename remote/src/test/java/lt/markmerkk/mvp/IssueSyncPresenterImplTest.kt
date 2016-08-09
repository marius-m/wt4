package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.merger.RemoteIssuePull
import lt.markmerkk.merger.RemoteMergeToolsProvider
import net.rcarz.jiraclient.Issue
import org.junit.Assert.*
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
class IssueSyncPresenterImplTest {

    val view: IssueSyncMvp.View = mock()
    val jiraInteractor: JiraInteractor = mock()
    val dataStorage: IDataStorage<LocalIssue> = mock()
    val remoteToolsProvider: RemoteMergeToolsProvider = mock()
    val remoteIssueMerge: RemoteIssuePull = mock()

    val presenter = IssueSyncPresenterImpl(
            view,
            remoteToolsProvider,
            jiraInteractor,
            dataStorage,
            Schedulers.immediate()
    )

    @Before
    fun setUp() {
        val validIssue: Issue = mock()
        whenever(jiraInteractor.jiraIssues())
                .thenReturn(Observable.just(
                        listOf(validIssue, validIssue, validIssue)
                ))
        whenever(remoteIssueMerge.call()).thenReturn(validIssue)
        whenever(remoteToolsProvider.issuePullMerger(any(), any()))
                .thenReturn(remoteIssueMerge)
    }

    @Test
    fun valid_triggerMerge() {
        // Assemble
        // Act
        presenter.sync()

        // Assert
        verify(remoteToolsProvider, times(3)).issuePullMerger(any(), any())
        verify(remoteIssueMerge, times(3)).call()
    }

    @Test
    fun valid_emitOnce() {
        // Assemble
        val filter: JiraFilter<Issue> = mock()
        val testSubscriber = TestSubscriber<List<Issue>>()

        // Act
        presenter.issueCacheObservable(filter)
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun valid_triggerDataChange() {
        // Assemble

        // Act
        presenter.sync()

        // Assert
        verify(dataStorage).notifyDataChange()
    }

    @Test
    fun error_triggerDataChange() {
        // Assemble
        reset(jiraInteractor)
        whenever(jiraInteractor.jiraIssues())
                .thenReturn(
                        Observable.error<List<Issue>>(IllegalStateException("error_getting_issue"))
                )

        // Act
        presenter.sync()

        // Assert
        verify(dataStorage).notifyDataChange()
    }
}
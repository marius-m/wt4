package lt.markmerkk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.LocalIssueBuilder
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.IDataStorage
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class JiraInteractorImplLocalIssuesOldTest {

    val clientProvider: JiraClientProvider = mock()
    val localStorage: IDataStorage<SimpleLog> = mock()
    val issueStorage: IDataStorage<LocalIssue> = mock()
    val searchSubscriber: JiraSearchSubscriber = mock()
    val worklogSubscriber: JiraWorklogSubscriber = mock()
    val interactor = JiraInteractorImpl(
            clientProvider,
            localStorage,
            issueStorage,
            searchSubscriber,
            worklogSubscriber,
            Schedulers.immediate()
    )
    val testSubscriber = TestSubscriber<List<LocalIssue>>()

    @Test
    fun noItems_emitEmptyList() {
        // Arrange
        // Act
        interactor.jiraLocalIssuesOld(1000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
    }

    @Test
    fun valid_emitOldItems() {
        // Arrange
        val validIssue = LocalIssueBuilder()
                .setDownloadMillis(900L)
                .setKey("test_key")
                .setProject("test")
                .setDescription("test_issue")
                .build()
        whenever(issueStorage.customQuery(any()))
                .thenReturn(listOf(validIssue, validIssue, validIssue))

        // Act
        interactor.jiraLocalIssuesOld(1000L)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValueCount(1)
        assertEquals(3, testSubscriber.onNextEvents[0].size)
    }

    @Test
    fun filter_onlyOldItems() {
        // Arrange
        val now = 1000L
        val oldIssue = LocalIssueBuilder()
                .setDownloadMillis(900L)
                .setKey("test_key")
                .setProject("test")
                .setDescription("test_issue")
                .build()
        val newIssue = LocalIssueBuilder()
                .setDownloadMillis(1100L)
                .setKey("test_key")
                .setProject("test")
                .setDescription("test_issue")
                .build()
        val currentIssue = LocalIssueBuilder()
                .setDownloadMillis(1000L)
                .setKey("test_key")
                .setProject("test")
                .setDescription("test_issue")
                .build()
        whenever(issueStorage.customQuery(any()))
                .thenReturn(listOf(
                        newIssue, oldIssue, currentIssue, newIssue, oldIssue, currentIssue
                ))

        // Act
        interactor.jiraLocalIssuesOld(now)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(testSubscriber)

        // Assert
        assertEquals(2, testSubscriber.onNextEvents[0].size)
        testSubscriber.onNextEvents[0].forEach {
            assertEquals(oldIssue, it)
        }
    }

}
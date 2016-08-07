package lt.markmerkk

import com.nhaarman.mockito_kotlin.mock
import net.rcarz.jiraclient.Issue
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import rx.observers.TestSubscriber

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-17
 */
class JiraInteractorImplIssuesTest {
    @Test
    @Ignore // Incomplete test
    fun valid_returnValidIssue() {
        // Arrange
        val testSubscriber = TestSubscriber<Issue>()
        val clientProvider: JiraClientProvider = mock()
        val searchSubscriber: JiraSearchSubsciber = mock()
        val worklogSubscriber: JiraWorklogSubscriber = mock()
        val interactor = JiraInteractorImpl(
                clientProvider,
                searchSubscriber,
                worklogSubscriber
        )

        // Act
        interactor.jiraIssues()
                .subscribe(testSubscriber)


        // Assert
        testSubscriber.assertNoErrors()
        val result = testSubscriber.onNextEvents
        assertEquals(1, result.size)
    }
}
package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.JiraWork
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-09
 */
class JiraInteractorImplSearchJqlForWorklogTest {
    val testSubscriber = TestSubscriber<List<JiraWork>>()
    val jiraClient: JiraClient = mock()
    val interactor = JiraInteractorImpl(
            "fake_host",
            "fake_username",
            "fake_password"
    )

    @Before
    fun setUp() {
        interactor.jiraClient = jiraClient
    }

    @Test
    fun valid_validResult() {
        // Arrange
        // Act
        interactor.searchJqlForWorklog(DateTime(1000), DateTime(2000))
                .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertNoErrors()
    }
}
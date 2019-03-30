package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.tickets.JiraSearchSubscriberImpl
import net.rcarz.jiraclient.Issue
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.slf4j.LoggerFactory
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.io.FileInputStream
import java.util.*

/**
 * Created by mariusmerkevicius on 1/29/16.
 */
@Ignore // Integration tests
class IntegrationJiraSearchSubscriberImplTest {
    private lateinit var clientProvider: JiraClientProvider
    val userSettings: UserSettings = mock()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val inputStream = FileInputStream("integration_test.properties")
        val properties = Properties()
        properties.load(inputStream)

        doReturn(properties["host"].toString()).whenever(userSettings).host
        doReturn(properties["username"].toString()).whenever(userSettings).username
        doReturn(properties["password"].toString()).whenever(userSettings).password
        doReturn(Const.DEFAULT_JQL_USER_ISSUES).whenever(userSettings).issueJql
        clientProvider = JiraClientProvider(userSettings)
    }

    @Test
    fun issuesWithWorklogs() {
        // Arrange
        val subscriber = TestSubscriber<Issue.SearchResult>()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime(formatter.parseDateTime("2015-05-15"))
        val endDate = DateTime(formatter.parseDateTime("2015-05-26"))

        // Act
        Observable.defer {
            JiraSearchSubscriberImpl(clientProvider, userSettings)
                    .workedIssuesObservable(
                            startDate.millis,
                            endDate.millis
                    )
        }
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(subscriber)

        // Assert
        val results = subscriber.onNextEvents
        logger.debug("Result: " + results)
        results.forEach {
            it.issues.forEach {
                logger.debug(it.toString())
            }
        }
        subscriber.assertNoErrors() // Should create successfully
    }

    @Test
    fun userIssues() {
        // Arrange
        val subscriber = TestSubscriber<Issue.SearchResult>()

        // Act
        Observable.defer {
            JiraSearchSubscriberImpl(clientProvider, userSettings)
                    .userIssuesObservable()
        }
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(subscriber)

        // Assert
        val results = subscriber.onNextEvents
        logger.debug("Result: " + results)
        results.forEach {
            it.issues.forEach {
                logger.debug(it.toString())
            }
        }
        subscriber.assertNoErrors() // Should create successfully
    }

    companion object {
        private val logger = LoggerFactory.getLogger(IntegrationJiraSearchSubscriberImplTest::class.java)!!
    }

}
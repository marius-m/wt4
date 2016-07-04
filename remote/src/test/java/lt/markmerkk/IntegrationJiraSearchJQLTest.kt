package lt.markmerkk

import net.rcarz.jiraclient.Issue
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
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
class IntegrationJiraSearchJQLTest {
    private lateinit var connector: JiraConnector

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val inputStream = FileInputStream("integration_test.properties")
        val properties = Properties()
        properties.load(inputStream)

        connector = JiraConnector(
                properties["host"] as String,
                properties["username"] as String,
                properties["password"] as String
        )
    }

    @Test
    fun test_observer() {
        // Arrange
        val subscriber = TestSubscriber<Issue.SearchResult>()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime(formatter.parseDateTime("2015-05-15"))
        val endDate = DateTime(formatter.parseDateTime("2015-05-26"))

        // Act
        Observable.create(connector)
                .flatMap {
                    Observable.create(JiraSearchJQL(
                            it,
                            String.format(
                                    JiraSearchJQL.DEFAULT_JQL_WORKLOG_TEMPLATE,
                                    formatter.print(startDate),
                                    formatter.print(endDate),
                                    connector.username
                            ),
                            "*all"))
                }
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(subscriber)

        // Assert
        val results = subscriber.onNextEvents
        logger.debug("Result: " + results)
        subscriber.assertNoErrors() // Should create successfully
    }

    companion object {
        val logger = LoggerFactory.getLogger(IntegrationJiraSearchJQLTest::class.java)
    }

}
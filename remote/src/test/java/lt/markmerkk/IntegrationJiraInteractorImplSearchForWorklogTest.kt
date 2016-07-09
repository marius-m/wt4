package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.observers.TestSubscriber
import java.io.FileInputStream
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
@Ignore
class IntegrationJiraInteractorImplSearchForWorklogTest {

    val logger: Logger = LoggerFactory.getLogger(IntegrationJiraInteractorImplSearchForWorklogTest::class.java)
    lateinit var observableGen: JiraInteractorImpl

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val inputStream = FileInputStream("integration_test.properties")
        val properties = Properties()
        properties.load(inputStream)

        val clientProvider = JiraClientProviderImpl(
                host = properties["host"].toString(),
                username = properties["username"].toString(),
                password = properties["password"].toString()
        )
        observableGen = JiraInteractorImpl(
                jiraClientProvider = clientProvider,
                jiraSearchSubsciber = JiraSearchSubscriberImpl(clientProvider),
                jiraWorklogSubscriber = JiraWorklogSubscriberImpl(clientProvider)
        )
    }

    @Test
    fun searchIssues() {
        val testSubscriber = TestSubscriber<Any>()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime(formatter.parseDateTime("2016-03-25"))
        val endDate = DateTime(formatter.parseDateTime("2016-04-26"))

        observableGen.jiraWorks(startDate, endDate)
                .subscribe({
                    logger.debug("Result: $it")
                }, {
                    logger.error("Error: $it")
                }, {
                    logger.debug("Complete")
                })
    }

}
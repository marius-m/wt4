package lt.markmerkk

import net.rcarz.jiraclient.Issue
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.io.FileInputStream
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class IntegrationJiraObservables2SearchForWorklogTest {

    val logger: Logger = LoggerFactory.getLogger(IntegrationJiraObservables2SearchForWorklogTest::class.java)
    lateinit var observableGen: JiraObservables2

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val inputStream = FileInputStream("integration_test.properties")
        val properties = Properties()
        properties.load(inputStream)

        observableGen = JiraObservables2(
                host = properties["host"].toString(),
                username = properties["username"].toString(),
                password = properties["password"].toString(),
                ioScheduler = Schedulers.immediate(),
                uiScheduler = Schedulers.immediate()
        )
    }

    @Test
    fun searchIssues() {
        val testSubscriber = TestSubscriber<Any>()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime(formatter.parseDateTime("2010-04-25"))
        val endDate = DateTime(formatter.parseDateTime("2010-04-26"))

        observableGen
                .clientObservable()
                .flatMap {
                    observableGen.searchJqlForWorklog(
                            observableGen.jqlForWorkIssuesFromDateObservable(startDate, endDate),
                            it
                    )
                }
                .subscribe({
                    logger.debug("Result: $it")
                }, {
                    logger.error("Error: $it")
                }, {
                    logger.debug("Complete")
                })
    }

}
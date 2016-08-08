package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.IDataStorage
import lt.markmerkk.mvp.UserSettings
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Ignore
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

        val userSettings: UserSettings = mock()
        doReturn(properties["host"].toString()).whenever(userSettings).host
        doReturn(properties["username"].toString()).whenever(userSettings).username
        doReturn(properties["password"].toString()).whenever(userSettings).password
        val clientProvider = JiraClientProviderImpl(userSettings)
        val dataStorage: IDataStorage<SimpleLog> = mock()
        observableGen = JiraInteractorImpl(
                jiraClientProvider = clientProvider,
                localStorage = dataStorage,
                jiraSearchSubscriber = JiraSearchSubscriberImpl(clientProvider),
                jiraWorklogSubscriber = JiraWorklogSubscriberImpl(clientProvider)
        )
    }

    @Test
    fun searchIssues() {
        val testSubscriber = TestSubscriber<Any>()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime(formatter.parseDateTime("2016-03-25")).millis
        val endDate = DateTime(formatter.parseDateTime("2016-04-26")).millis

        observableGen.jiraRemoteWorks(startDate, endDate)
                .subscribe({
                    logger.debug("Result: $it")
                }, {
                    logger.error("Error: $it")
                }, {
                    logger.debug("Complete")
                })
    }

}
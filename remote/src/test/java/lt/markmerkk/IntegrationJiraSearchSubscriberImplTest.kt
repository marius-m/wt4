package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.mvp.UserSettings
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
class IntegrationJiraSearchSubscriberImplTest {
    private lateinit var clientProvider: JiraClientProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val inputStream = FileInputStream("integration_test.properties")
        val properties = Properties()
        properties.load(inputStream)

        val userSettings: UserSettings = mock()
        doReturn(properties["host"].toString()).whenever(userSettings).host
        doReturn(properties["username"].toString()).whenever(userSettings).username
        doReturn(properties["password"].toString()).whenever(userSettings).password
        clientProvider = JiraClientProviderImpl(userSettings)
    }

    @Test
    fun test_observer() {
        // Arrange
        val subscriber = TestSubscriber<Issue.SearchResult>()
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime(formatter.parseDateTime("2015-05-15"))
        val endDate = DateTime(formatter.parseDateTime("2015-05-26"))

        // Act
        Observable.create(JiraSearchSubscriberImpl(clientProvider))
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(subscriber)

        // Assert
        val results = subscriber.onNextEvents
        logger.debug("Result: " + results)
        subscriber.assertNoErrors() // Should create successfully
    }

    companion object {
        val logger = LoggerFactory.getLogger(IntegrationJiraSearchSubscriberImplTest::class.java)!!
    }

}
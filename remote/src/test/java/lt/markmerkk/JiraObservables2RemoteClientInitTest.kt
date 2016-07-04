package lt.markmerkk

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import lt.markmerkk.interfaces.IRemoteLoadListener
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import org.joda.time.DateTime
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class JiraObservables2RemoteClientInitTest {
    val observableGen = JiraObservables2(
            host = "fake_host",
            username = "fake_username",
            password = "fake_password",
            ioScheduler = Schedulers.immediate(),
            uiScheduler = Schedulers.immediate()
    )

    @Test
    fun validClient_initJiraClient() {
        assertNull(observableGen.jiraClient)

        observableGen
                .clientObservable()
                .subscribe()

        assertNotNull(observableGen.jiraClient)
    }

    @Test
    fun reuseJiraClient_multipleSync() {
        assertNull(observableGen.jiraClient)

        observableGen
                .clientObservable()
                .subscribe()

        val newJiraClient = observableGen.jiraClient

        observableGen
                .clientObservable()
                .subscribe()
        observableGen
                .clientObservable()
                .subscribe()

        assertEquals(newJiraClient, observableGen.jiraClient)
    }

    @Test
    fun invalidClient_initJiraClient() {
        val observableGen2 = JiraObservables2(
                host = "", // invalid
                username = "", // invalid
                password = "", // invalid
                ioScheduler = Schedulers.immediate(),
                uiScheduler = Schedulers.immediate()
        )
        val testSubscriber = TestSubscriber<JiraClient>()

        observableGen2
                .clientObservable()
                .subscribe(testSubscriber)

        testSubscriber.assertError(JiraException::class.java)
    }

}
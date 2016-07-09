package lt.markmerkk

import com.nhaarman.mockito_kotlin.mock
import net.rcarz.jiraclient.JiraClient
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import rx.observers.TestSubscriber
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class JiraClientProviderImplInitTest {
    val jiraClientProvider = JiraClientProviderImpl(
            host = "fake_host",
            username = "fake_username",
            password = "fake_password"
    )

    @Test
    fun validClient_initJiraClient() {
        assertNull(jiraClientProvider.jiraClient)

        jiraClientProvider
                .clientObservable()
                .subscribe()

        assertNotNull(jiraClientProvider.jiraClient)
    }

    @Test
    fun reuseJiraClient_multipleSync() {
        assertNull(jiraClientProvider.jiraClient)

        jiraClientProvider
                .clientObservable()
                .subscribe()

        val newJiraClient = jiraClientProvider.jiraClient

        jiraClientProvider
                .clientObservable()
                .subscribe()
        jiraClientProvider
                .clientObservable()
                .subscribe()

        assertEquals(newJiraClient, jiraClientProvider.jiraClient)
    }

    @Test
    fun invalidClient_initJiraClient() {
        val clientProvider = JiraClientProviderImpl(
                host = "", // invalid
                username = "", // invalid
                password = ""
        )
        val testSubscriber = TestSubscriber<JiraClient>()

        clientProvider
                .clientObservable()
                .subscribe(testSubscriber)

        testSubscriber.assertError(IllegalStateException::class.java)
    }

}
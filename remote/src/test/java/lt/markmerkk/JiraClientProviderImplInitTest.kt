package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.UserSettings
import net.rcarz.jiraclient.JiraClient
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import kotlin.test.assertEquals

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-03
 */
class JiraClientProviderImplInitTest {
    val fakeUserSettings: UserSettings = mock()
    val jiraClientProvider = JiraClientProviderImpl(
            userSettings = fakeUserSettings
    )

    @Before
    fun setUp() {
        doReturn("valid_host").whenever(fakeUserSettings).host
        doReturn("valid_username").whenever(fakeUserSettings).username
        doReturn("valid_password").whenever(fakeUserSettings).password
    }

    @Test
    fun validClient_initJiraClient() {
        assertNull(jiraClientProvider.jiraClient)

        Observable.just(jiraClientProvider.client())
                .subscribe()

        assertNotNull(jiraClientProvider.jiraClient)
    }

    @Test
    fun reuseJiraClient_multipleSync() {
        assertNull(jiraClientProvider.jiraClient)

        Observable.just(jiraClientProvider.client())
                .subscribe()

        val newJiraClient = jiraClientProvider.jiraClient

        Observable.just(jiraClientProvider.client())
                .subscribe()
        Observable.just(jiraClientProvider.client())
                .subscribe()

        assertEquals(newJiraClient, jiraClientProvider.jiraClient)
    }

    @Test
    fun invalidClient_initJiraClient() {
        reset(fakeUserSettings)
        doReturn("").whenever(fakeUserSettings).host
        doReturn("").whenever(fakeUserSettings).username
        doReturn("").whenever(fakeUserSettings).password
        val clientProvider = JiraClientProviderImpl(fakeUserSettings)
        val testSubscriber = TestSubscriber<JiraClient>()

        Observable.defer { Observable.just(clientProvider.client()) }
                .subscribe(testSubscriber)

        testSubscriber.assertError(IllegalStateException::class.java)
    }

}
package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.JiraCreds
import net.rcarz.jiraclient.JiraClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single

class JiraClientProviderCacheTest {
    @Mock lateinit var userSettings: UserSettings
    @Mock lateinit var  jiraClient: JiraClient

    lateinit var jiraClientProvider: JiraClientProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraClientProvider = JiraClientProvider(userSettings)
    }

    @Test
    fun valid_createJiraClient() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.clientStream()
                .test()

        // Assert
        result.assertValueCount(1)
        result.assertNoErrors()
    }

    @Test
    fun valid_multipleCall_cacheClient() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")
        jiraClientProvider.cacheCreds = JiraCreds(
                hostname = "valid_host",
                username = "valid_username",
                password = "valid_pass"
        )
        jiraClientProvider.jiraClient = jiraClient

        // Act
        val result1 = jiraClientProvider.clientStream().test()
        val result2 = jiraClientProvider.clientStream().test()
        val result3 = jiraClientProvider.clientStream().test()

        // Assert
        result1.assertNoErrors()
        result1.assertValue(jiraClient)
        result2.assertNoErrors()
        result2.assertValue(jiraClient)
        result3.assertNoErrors()
        result3.assertValue(jiraClient)
    }

    @Test
    fun multipleCall_hostChangeInMiddle_recreateClient() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result1 = jiraClientProvider.clientStream().test()
        whenever(userSettings.host).thenReturn("valid_host2")
        val result2 = jiraClientProvider.clientStream().test()

        // Assert
        assertNotEquals(result1.onNextEvents.first(), result2.onNextEvents.first())
    }

    @Test
    fun noHost_throwError() {
        // Arrange
        whenever(userSettings.host).thenReturn("")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.clientStream().test()

        // Assert
        result.assertError(IllegalArgumentException::class.java)
    }

    @Test
    fun noUsername_throwError() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.clientStream().test()

        // Assert
        result.assertError(java.lang.IllegalArgumentException::class.java)
    }

    @Test
    fun noPass_throwError() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("")

        // Act
        val result = jiraClientProvider.clientStream().test()

        // Assert
        result.assertError(java.lang.IllegalArgumentException::class.java)
    }

}
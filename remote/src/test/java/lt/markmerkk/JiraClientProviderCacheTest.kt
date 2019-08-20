package lt.markmerkk

import com.nhaarman.mockitokotlin2.whenever
import net.rcarz.jiraclient.JiraClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

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
        whenever(userSettings.password).thenReturn("valid_password")
        val initialClient = jiraClientProvider.client(
                hostname = "valid_host",
                username = "valid_username",
                password = "valid_password"
        )

        // Act
        val result1 = jiraClientProvider.clientFromCache()
        val result2 = jiraClientProvider.clientFromCache()
        val result3 = jiraClientProvider.clientFromCache()

        // Assert
        assertThat(result1).isEqualTo(initialClient)
        assertThat(result2).isEqualTo(initialClient)
        assertThat(result3).isEqualTo(initialClient)
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
        assertThat(result1.onNextEvents.first()).isNotEqualTo(result2.onNextEvents.first())
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
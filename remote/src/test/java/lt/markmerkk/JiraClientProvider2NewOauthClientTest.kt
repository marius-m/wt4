package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraClientProvider2NewOauthClientTest {

    @Mock
    lateinit var userSettings: UserSettings
    lateinit var jiraClientProvider: JiraClientProviderOauth

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraClientProvider = JiraClientProviderOauth(userSettings)
    }

    @Test
    fun valid() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "valid_key",
                consumerKey = "consumer_key"
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "valid_secret",
                accessKey = "access_key"
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        val result = jiraClientProvider.newClient()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun sameClient() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "valid_key",
                consumerKey = "consumer_key"
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "valid_secret",
                accessKey = "access_key"
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        val result1 = jiraClientProvider.newClient()

        // Assert
        assertThat(result1).isNotNull()

        // Act
        val result2 = jiraClientProvider.newClient()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }

    @Test
    fun markAsError() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "valid_key",
                consumerKey = "consumer_key"
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "valid_secret",
                accessKey = "access_key"
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        val result1 = jiraClientProvider.newClient()

        // Assert
        assertThat(result1).isNotNull()

        // Act
        jiraClientProvider.markAsError()
        val result2 = jiraClientProvider.newClient()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }
}
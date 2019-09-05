package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.exceptions.AuthException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraClientProvider2OauthClientTest {

    @Mock lateinit var userSettings: UserSettings
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
        val result = jiraClientProvider.client()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test(expected = AuthException::class)
    fun hasError() {
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
        val result = jiraClientProvider.client()

        // Assert
        assertThat(result).isNotNull()

        // Act
        jiraClientProvider.markAsError()
        jiraClientProvider.client() // error client
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
        val result1 = jiraClientProvider.client()

        // Assert
        assertThat(result1).isNotNull()

        // Act
        val result2 = jiraClientProvider.client()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isEqualTo(result2)
    }

    @Test
    fun differentCredentials() {
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
        val result1 = jiraClientProvider.client()

        // Assert
        assertThat(result1).isNotNull()

        // Assemble
        val jiraCreds2 = JiraOAuthCreds(
                tokenSecret = "secret2",
                accessKey = "access_key2"
        )
        doReturn(jiraCreds2).whenever(userSettings).jiraOAuthCreds()

        // Act
        val result2 = jiraClientProvider.client()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }

    @Test(expected = AuthException::class)
    fun invalidHost() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "",
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
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidPrivateKey() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "",
                consumerKey = "consumer_key"
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "valid_secret",
                accessKey = "access_key"
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidConsumerKey() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "valid_key",
                consumerKey = ""
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "valid_secret",
                accessKey = "access_key"
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidTokenSecret() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "valid_key",
                consumerKey = "consumer_key"
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "",
                accessKey = "access_key"
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidAccessKey() {
        // Assemble
        val jiraPreset = JiraOAuthPreset(
                host = "valid_host",
                privateKey = "valid_key",
                consumerKey = "consumer_key"
        )
        val jiraCreds = JiraOAuthCreds(
                tokenSecret = "token_secret",
                accessKey = ""
        )
        doReturn(jiraPreset).whenever(userSettings).jiraOAuthPreset()
        doReturn(jiraCreds).whenever(userSettings).jiraOAuthCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

}
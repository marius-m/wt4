package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.utils.AccountAvailablility
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AccountAvailabilityInteractorOAuthTest {

    @Mock lateinit var userSettings: UserSettings
    @Mock lateinit var jiraClientProvider: JiraClientProvider
    lateinit var accountAvailablility: AccountAvailablility

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        accountAvailablility = AccountAvailabilityOAuth(userSettings, jiraClientProvider)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Mocks.createJiraOAuthPreset(
                host = "hostname",
                privateKey = "private_key",
                consumerKey = "consumer_key"
        )).whenever(userSettings).jiraOAuthPreset()
        doReturn(Mocks.createJiraOAuthCreds(
                tokenSecret = "tokey_secret",
                accessKey = "access_key"
        )).whenever(userSettings).jiraOAuthCreds()
        doReturn(Mocks.createJiraUser()).whenever(userSettings).jiraUser()

        val result = accountAvailablility.isAccountReadyForSync()

        assertThat(result).isTrue()
    }

    @Test
    fun noPreset() {
        // Assemble
        doReturn(Mocks.createJiraOAuthPreset(
                host = "",
                privateKey = "",
                consumerKey = ""
        )).whenever(userSettings).jiraOAuthPreset()
        doReturn(Mocks.createJiraOAuthCreds(
                tokenSecret = "tokey_secret",
                accessKey = "access_key"
        )).whenever(userSettings).jiraOAuthCreds()
        doReturn(Mocks.createJiraUser()).whenever(userSettings).jiraUser()

        val result = accountAvailablility.isAccountReadyForSync()

        assertThat(result).isFalse()
    }

    @Test
    fun noCreds() {
        // Assemble
        doReturn(Mocks.createJiraOAuthPreset(
                host = "hostname",
                privateKey = "private_key",
                consumerKey = "consumer_key"
        )).whenever(userSettings).jiraOAuthPreset()
        doReturn(Mocks.createJiraOAuthCreds(
                tokenSecret = "",
                accessKey = ""
        )).whenever(userSettings).jiraOAuthCreds()
        doReturn(Mocks.createJiraUser()).whenever(userSettings).jiraUser()

        val result = accountAvailablility.isAccountReadyForSync()

        assertThat(result).isFalse()
    }

    @Test
    fun emptyUser() {
        // Assemble
        doReturn(Mocks.createJiraOAuthPreset(
                host = "hostname",
                privateKey = "private_key",
                consumerKey = "consumer_key"
        )).whenever(userSettings).jiraOAuthPreset()
        doReturn(Mocks.createJiraOAuthCreds(
                tokenSecret = "tokey_secret",
                accessKey = "access_key"
        )).whenever(userSettings).jiraOAuthCreds()
        doReturn(Mocks.createJiraUserEmpty()).whenever(userSettings).jiraUser()

        val result = accountAvailablility.isAccountReadyForSync()

        assertThat(result).isFalse()
    }

    @Test
    fun clientError() {
        // Assemble
        doReturn(Mocks.createJiraOAuthPreset(
                host = "hostname",
                privateKey = "private_key",
                consumerKey = "consumer_key"
        )).whenever(userSettings).jiraOAuthPreset()
        doReturn(Mocks.createJiraOAuthCreds(
                tokenSecret = "tokey_secret",
                accessKey = "access_key"
        )).whenever(userSettings).jiraOAuthCreds()
        doReturn(Mocks.createJiraUser()).whenever(userSettings).jiraUser()
        doReturn(true).whenever(jiraClientProvider).hasError()

        val result = accountAvailablility.isAccountReadyForSync()

        assertThat(result).isFalse()
    }

}
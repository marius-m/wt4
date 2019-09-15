package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraMocks
import lt.markmerkk.JiraOAuthCreds
import lt.markmerkk.JiraOAuthPreset
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraLinkGeneratorOAuthWebLinkTest {

    @Mock lateinit var view: JiraLinkGenerator.View
    @Mock lateinit var userSettings: UserSettings
    lateinit var jiraLinkGenerator: JiraLinkGeneratorOAuth

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraLinkGenerator = JiraLinkGeneratorOAuth(
                view = view,
                userSettings = userSettings
        )
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(JiraOAuthPreset("valid", "valid", "valid")).whenever(userSettings)
                .jiraOAuthPreset()
        doReturn(JiraOAuthCreds("valid", "valid")).whenever(userSettings)
                .jiraOAuthCreds()
        doReturn(JiraMocks.createJiraUser()).whenever(userSettings)
                .jiraUser()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("DEV-123")

        // Assert
        assertThat(result).isEqualTo("valid/browse/DEV-123")
    }

    @Test
    fun invalidCode() {
        // Assemble
        doReturn(JiraOAuthPreset("valid", "valid", "valid")).whenever(userSettings)
                .jiraOAuthPreset()
        doReturn(JiraOAuthCreds("valid", "valid")).whenever(userSettings)
                .jiraOAuthCreds()
        doReturn(JiraMocks.createJiraUser()).whenever(userSettings)
                .jiraUser()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("invalid")

        // Assert
        assertThat(result).isEqualTo("")
    }

    @Test
    fun noUser() {
        // Assemble
        doReturn(JiraOAuthPreset("valid", "valid", "valid")).whenever(userSettings)
                .jiraOAuthPreset()
        doReturn(JiraOAuthCreds("valid", "valid")).whenever(userSettings)
                .jiraOAuthCreds()
        doReturn(JiraMocks.createJiraUserEmpty()).whenever(userSettings) // empty user
                .jiraUser()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("DEV-123")

        // Assert
        assertThat(result).isEqualTo("")
    }

    @Test
    fun noCreds() {
        // Assemble
        doReturn(JiraOAuthPreset("valid", "valid", "valid")).whenever(userSettings)
                .jiraOAuthPreset()
        doReturn(JiraOAuthCreds("", "")).whenever(userSettings)
                .jiraOAuthCreds()
        doReturn(JiraMocks.createJiraUser()).whenever(userSettings)
                .jiraUser()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("DEV-123")

        // Assert
        assertThat(result).isEqualTo("")
    }

    @Test
    fun noPreset() { // not sure possible
        // Assemble
        doReturn(JiraOAuthPreset("", "", "")).whenever(userSettings)
                .jiraOAuthPreset()
        doReturn(JiraOAuthCreds("valid", "valid")).whenever(userSettings)
                .jiraOAuthCreds()
        doReturn(JiraMocks.createJiraUser()).whenever(userSettings)
                .jiraUser()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("DEV-123")

        // Assert
        assertThat(result).isEqualTo("")
    }
}
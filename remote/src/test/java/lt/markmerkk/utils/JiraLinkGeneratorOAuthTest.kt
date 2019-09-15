package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraMocks
import lt.markmerkk.JiraOAuthCreds
import lt.markmerkk.JiraOAuthPreset
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketCode
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraLinkGeneratorOAuthTest {

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
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).showCopyLink(eq(TicketCode.new("DEV-123")), any())
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
        jiraLinkGenerator.handleTicketInput("invalid")

        // Assert
        verify(view).hideCopyLink()
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
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).hideCopyLink()
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
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).hideCopyLink()
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
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).hideCopyLink()
    }
}
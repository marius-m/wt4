package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketCode
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraLinkGeneratorBasicTest {

    @Mock lateinit var view: JiraLinkGenerator.View
    @Mock lateinit var userSettings: UserSettings
    lateinit var jiraLinkGenerator: JiraLinkGeneratorBasic

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraLinkGenerator = JiraLinkGeneratorBasic(
                view = view,
                userSettings = userSettings
        )
    }

    @Test
    fun valid() {
        // Assemble
        doReturn("valid_host").whenever(userSettings).host

        // Act
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).showCopyLink(eq(TicketCode.new("DEV-123")), any())
    }

    @Test
    fun invalidCode() {
        // Assemble
        doReturn("valid_host").whenever(userSettings).host

        // Act
        jiraLinkGenerator.handleTicketInput("invalid")

        // Assert
        verify(view).hideCopyLink()
    }

    @Test
    fun noHost() {
        // Assemble
        doReturn("").whenever(userSettings).host

        // Act
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).hideCopyLink()
    }
}
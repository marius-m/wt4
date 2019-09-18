package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.entities.TicketCode
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraLinkGeneratorOAuthTest {

    @Mock lateinit var view: JiraLinkGenerator.View
    @Mock lateinit var accountAvailablility: AccountAvailablility

    lateinit var jiraLinkGenerator: JiraLinkGeneratorOAuth

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraLinkGenerator = JiraLinkGeneratorOAuth(
                view = view,
                accountAvailability = accountAvailablility
        )
    }

    @Test
    fun valid() {
        // Assemble
        doReturn("host").whenever(accountAvailablility).host()
        doReturn(true).whenever(accountAvailablility).isAccountReadyForSync()

        // Act
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).showCopyLink(eq(TicketCode.new("DEV-123")), any())
    }

    @Test
    fun invalidCode() {
        // Assemble
        doReturn("host").whenever(accountAvailablility).host()
        doReturn(true).whenever(accountAvailablility).isAccountReadyForSync()

        // Act
        jiraLinkGenerator.handleTicketInput("invalid")

        // Assert
        verify(view).hideCopyLink()
    }

    @Test
    fun accountNotAvailable() {
        // Assemble
        doReturn("host").whenever(accountAvailablility).host()
        doReturn(false).whenever(accountAvailablility).isAccountReadyForSync()

        // Act
        jiraLinkGenerator.handleTicketInput("DEV-123")

        // Assert
        verify(view).hideCopyLink()
    }
}
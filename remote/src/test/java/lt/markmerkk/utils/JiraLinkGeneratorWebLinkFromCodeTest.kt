package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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

class JiraLinkGeneratorWebLinkFromCodeTest {

    @Test
    fun valid() {
        // Assemble
        // Act
        val link = JiraLinkGenerator.webLinkFromCode(
                host = "https://jira.lt",
                ticketCode = TicketCode.new("DEV-123")
        )

        // Assert
        assertThat(link).isEqualTo("https://jira.lt/browse/DEV-123")
    }

    @Test
    fun hasTrailingSlash() {
        // Assemble
        // Act
        val link = JiraLinkGenerator.webLinkFromCode(
                host = "https://jira.lt/",
                ticketCode = TicketCode.new("DEV-123")
        )

        // Assert
        assertThat(link).isEqualTo("https://jira.lt/browse/DEV-123")
    }

    @Test
    fun noHost() {
        // Assemble
        // Act
        val link = JiraLinkGenerator.webLinkFromCode(
                host = "",
                ticketCode = TicketCode.new("DEV-123")
        )

        // Assert
        assertThat(link).isEmpty()
    }

    @Test
    fun noCode() {
        // Assemble
        // Act
        val link = JiraLinkGenerator.webLinkFromCode(
                host = "https://jira.lt",
                ticketCode = TicketCode.asEmpty()
        )

        // Assert
        assertThat(link).isEmpty()
    }

}
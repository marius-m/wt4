package lt.markmerkk.tickets

import lt.markmerkk.Mocks
import lt.markmerkk.entities.Ticket
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TicketLoaderFilterTest {

    private val tickets: List<Ticket> = listOf(
            "google",           // TTS-1
            "bing",             // TTS-2
            "facebook",         // TTS-3
            "linkedin",         // TTS-4
            "twitter",          // TTS-5
            "googleplus",       // TTS-6
            "bingnews",         // TTS-7
            "plexoogl"          // TTS-8
    ).mapIndexed { index: Int, description: String ->
        Mocks.createTicket(code = "TTS-00${index + 1}", description = description)
    }

    @Test
    fun noTickets() {
        // Act
        val result = TicketLoader.filter(
                inputTickets = emptyList(),
                searchInput = ""
        )

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun noSearch() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = ""
        )

        // Assert
        assertThat(result.size).isEqualTo(tickets.size)
    }

    @Test
    fun tooLittleInput() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = "a"
        )

        // Assert
        assertThat(result.size).isEqualTo(tickets.size)
    }

    @Test
    fun validDescription() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = "goog"
        )

        // Assert
        assertThat(result).contains(
                tickets[0], // google
                tickets[5], // googleplus
                tickets[7]  // plexoogl
        )
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    fun ticketProject() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = "TTS"
        )

        // Assert
        assertThat(result.size).isEqualTo(tickets.size)
    }

    @Test
    fun ticketProject_lowerCase() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = "tt"
        )

        // Assert
        assertThat(result.size).isEqualTo(tickets.size)
    }

    @Test
    fun concreteTicket() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = "TTS-005"
        )

        // Assert
        assertThat(result.size).isEqualTo(1)
        assertThat(result).containsExactly(tickets[4])
    }

    @Test
    fun onlyTicketNumber() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = tickets,
                searchInput = "005"
        )

        // Assert
        assertThat(result.size).isEqualTo(1)
        assertThat(result).containsExactly(tickets[4])
    }
}
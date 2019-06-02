package lt.markmerkk.tickets

import lt.markmerkk.Mocks
import lt.markmerkk.entities.Ticket
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TicketLoaderFilterTest {

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
                inputTickets = MocksTickets.tickets,
                searchInput = ""
        )

        // Assert
        assertThat(result.size).isEqualTo(MocksTickets.tickets.size)
    }

    @Test
    fun tooLittleInput() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = MocksTickets.tickets,
                searchInput = "a"
        )

        // Assert
        assertThat(result.size).isEqualTo(MocksTickets.tickets.size)
    }

    @Test
    fun validDescription() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = MocksTickets.tickets,
                searchInput = "goog"
        )

        // Assert
        assertThat(result).contains(
                MocksTickets.tickets[0], // google
                MocksTickets.tickets[5], // googleplus
                MocksTickets.tickets[7]  // plexoogl
        )
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    fun ticketProject() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = MocksTickets.tickets,
                searchInput = "TTS"
        )

        // Assert
        assertThat(result.size).isEqualTo(MocksTickets.tickets.size)
    }

    @Test
    fun ticketProject_lowerCase() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = MocksTickets.tickets,
                searchInput = "tt"
        )

        // Assert
        assertThat(result.size).isEqualTo(MocksTickets.tickets.size)
    }

    @Test
    fun concreteTicket() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = MocksTickets.tickets,
                searchInput = "TTS-115"
        )

        // Assert
        assertThat(result.size).isEqualTo(1)
        assertThat(result).containsExactly(MocksTickets.tickets[4])
    }

    @Test
    fun onlyTicketNumber() {
        // Assemble
        // Act
        val result = TicketLoader.filter(
                inputTickets = MocksTickets.tickets,
                searchInput = "115"
        )

        // Assert
        assertThat(result.size).isEqualTo(1)
        assertThat(result).containsExactly(MocksTickets.tickets[4])
    }
}
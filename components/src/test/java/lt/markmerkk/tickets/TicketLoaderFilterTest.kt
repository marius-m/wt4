package lt.markmerkk.tickets

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
                MocksTickets.tickets[5].withScore(90),
                MocksTickets.tickets[0].withScore(90),
                MocksTickets.tickets[7].withScore(68)
        )
    }

}
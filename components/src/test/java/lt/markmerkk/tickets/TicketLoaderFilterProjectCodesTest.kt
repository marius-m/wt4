package lt.markmerkk.tickets

import lt.markmerkk.Mocks
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TicketLoaderFilterProjectCodesTest {

    @Test
    fun noTickets() {
        val resultCodes = TicketLoader.filterProjectCodes(emptyList())

        assertThat(resultCodes).isEmpty()
    }

    @Test
    fun validTicket() {
        val resultCodes = TicketLoader.filterProjectCodes(
                listOf(
                        Mocks.createTicket(code = "DEV-123")
                )
        )

        assertThat(resultCodes).containsExactly("DEV")
    }

    @Test
    fun multipleTickets() {
        val resultCodes = TicketLoader.filterProjectCodes(
                listOf(
                        Mocks.createTicket(code = "DEV-123"),
                        Mocks.createTicket(code = "DEV-111")
                )
        )

        assertThat(resultCodes).containsExactly("DEV")
    }

    @Test
    fun multipleTickets_diffProjects() {
        val resultCodes = TicketLoader.filterProjectCodes(
                listOf(
                        Mocks.createTicket(code = "DEV-123"),
                        Mocks.createTicket(code = "DEV-111"),
                        Mocks.createTicket(code = "WT-110")
                )
        )

        assertThat(resultCodes).containsExactly("DEV", "WT")
    }
}
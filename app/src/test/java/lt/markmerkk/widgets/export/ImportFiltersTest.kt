package lt.markmerkk.widgets.export

import org.assertj.core.api.Assertions
import org.junit.Test

class ImportFiltersTest {

    private val defaultProjectFilter: String = "default_filter"
    private val importFilters = ImportFilters(
        defaultProjectFilter = defaultProjectFilter,
    )

    @Test
    fun noAction() {
        // Assemble
        val action = IFActionNoAction

        // Act
        val result = importFilters.filter(action = action)

        // Assert
        Assertions.assertThat(result).isEqualTo(
            ImportFilterResultState(
                action = action,
                isSelectNoTickets = false,
                isSelectTicketFromComments = false,
                isSelectTicketFilter = true,
                isEnabledTicketFilter = true,
                ticketFilter = defaultProjectFilter,
            )
        )
    }

    @Test
    fun noTickets() {
        // Assemble
        val action = IFActionNoTicketCode

        // Act
        val result = importFilters.filter(action = action)

        // Assert
        Assertions.assertThat(result).isEqualTo(
            ImportFilterResultState(
                action = action,
                isSelectNoTickets = true,
                isSelectTicketFromComments = false,
                isSelectTicketFilter = false,
                isEnabledTicketFilter = false,
                ticketFilter = defaultProjectFilter,
            )
        )
    }

    @Test
    fun ticketFromComment() {
        // Assemble
        val action = IFActionTicketFromComments

        // Act
        val result = importFilters.filter(action = action)

        // Assert
        Assertions.assertThat(result).isEqualTo(
            ImportFilterResultState(
                action = action,
                isSelectNoTickets = false,
                isSelectTicketFromComments = true,
                isSelectTicketFilter = false,
                isEnabledTicketFilter = false,
                ticketFilter = defaultProjectFilter,
            )
        )
    }

    @Test
    fun projectFilterDefault() {
        // Assemble
        val action = IFActionTicketProjectFilterDefault

        // Act
        val result = importFilters.filter(action = action)

        // Assert
        Assertions.assertThat(result).isEqualTo(
            ImportFilterResultState(
                action = action,
                isSelectNoTickets = false,
                isSelectTicketFromComments = false,
                isSelectTicketFilter = true,
                isEnabledTicketFilter = true,
                ticketFilter = defaultProjectFilter,
            )
        )
    }

    @Test
    fun projectFilter() {
        // Assemble
        val action = IFActionTicketProjectFilter(
            filter = "filter1"
        )

        // Act
        val result = importFilters.filter(action = action)

        // Assert
        Assertions.assertThat(result).isEqualTo(
            ImportFilterResultState(
                action = action,
                isSelectNoTickets = false,
                isSelectTicketFromComments = false,
                isSelectTicketFilter = true,
                isEnabledTicketFilter = true,
                ticketFilter = "filter1",
            )
        )
    }
}
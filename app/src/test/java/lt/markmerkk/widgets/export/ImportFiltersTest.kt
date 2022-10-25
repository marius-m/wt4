package lt.markmerkk.widgets.export

import org.assertj.core.api.Assertions
import org.junit.Test

class ImportFiltersTest {

    private val defaultProjectFilter: String = "default_filter"
    private val importFilters = ImportFilters()

    @Test
    fun clear() {
        // Assemble
        val action = IFActionClear

        // Act
        val result = importFilters.filter(action = action)

        // Assert
        Assertions.assertThat(result).isEqualTo(
            ImportFilterResultState(
                action = action,
                isSelectNoChanges = true,
                isSelectNoTickets = false,
                isSelectTicketFromComments = false,
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
                isSelectNoChanges = false,
                isSelectNoTickets = true,
                isSelectTicketFromComments = false,
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
                isSelectNoChanges = false,
                isSelectNoTickets = false,
                isSelectTicketFromComments = true,
            )
        )
    }
}
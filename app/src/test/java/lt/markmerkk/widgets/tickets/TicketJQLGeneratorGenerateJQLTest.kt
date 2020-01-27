package lt.markmerkk.widgets.tickets

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TicketJQLGeneratorGenerateJQLTest {

    @Test
    fun noStatuses_noUser() {
        // Act
        val result = TicketJQLGenerator.generateJQL(
                enabledStatuses = emptyList(),
                onlyCurrentUser = false
        )

        // Assert
        assertThat(result).isEqualTo("")
    }

    @Test
    fun noStatuses() {
        // Act
        val result = TicketJQLGenerator.generateJQL(
                enabledStatuses = emptyList(),
                onlyCurrentUser = true
        )

        // Assert
        assertThat(result).isEqualTo("(assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())")
    }

    @Test
    fun status() {
        // Act
        val result = TicketJQLGenerator.generateJQL(
                enabledStatuses = listOf("closed"),
                onlyCurrentUser = true
        )

        // Assert
        assertThat(result).isEqualTo("(status in ('closed')) AND (assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())")
    }

    @Test
    fun multipleStatus() {
        // Act
        val result = TicketJQLGenerator.generateJQL(
                enabledStatuses = listOf("closed", "done"),
                onlyCurrentUser = true
        )

        // Assert
        assertThat(result).isEqualTo("(status in ('closed','done')) AND (assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())")
    }

    @Test
    fun noUserInclude() {
        // Act
        val result = TicketJQLGenerator.generateJQL(
                enabledStatuses = listOf("closed"),
                onlyCurrentUser = false
        )

        // Assert
        assertThat(result).isEqualTo("(status in ('closed'))")
    }
}
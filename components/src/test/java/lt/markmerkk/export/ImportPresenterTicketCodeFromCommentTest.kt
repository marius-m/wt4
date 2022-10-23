package lt.markmerkk.export

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.export.ImportPresenter.Companion.extractTicketCodeFromComment
import org.assertj.core.api.Assertions
import org.junit.Test

class ImportPresenterTicketCodeFromCommentTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun valid() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            comment = "DEV-123 Some other random comment"
        )

        // Act
        val result = log.extractTicketCodeFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            TicketCode.new("DEV-123")
        )
    }

    @Test
    fun noCode() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            comment = "Some other random comment"
        )

        // Act
        val result = log.extractTicketCodeFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            TicketCode.asEmpty()
        )
    }

    @Test
    fun wholeUrl() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            comment = "https://somejira.jira.com/ticket/DEV-123 Some other random comment"
        )

        // Act
        val result = log.extractTicketCodeFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            TicketCode.new("DEV-123")
        )
    }

    @Test
    fun urlWithParenthesis() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            comment = "(https://somejira.jira.com/ticket/DEV-123) Some other random comment"
        )

        // Act
        val result = log.extractTicketCodeFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            TicketCode.new("DEV-123")
        )
    }
}
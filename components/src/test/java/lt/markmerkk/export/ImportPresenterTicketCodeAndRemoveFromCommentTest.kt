package lt.markmerkk.export

import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.export.ImportPresenter.Companion.extractTicketCodeAndRemoveFromComment
import org.assertj.core.api.Assertions
import org.junit.Test

class ImportPresenterTicketCodeAndRemoveFromCommentTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun valid() {
        // Assemble
        val log = Mocks.createLog(
            timeProvider = timeProvider,
            comment = "DEV-123 Some other random comment"
        )

        // Act
        val result = log.extractTicketCodeAndRemoveFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                timeProvider,
                code = "DEV-123",
                comment = "Some other random comment"
            )
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
        val result = log.extractTicketCodeAndRemoveFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                timeProvider,
                code = "",
                comment = "Some other random comment"
            )
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
        val result = log.extractTicketCodeAndRemoveFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                timeProvider,
                code = "DEV-123",
                comment = "Some other random comment"
            )
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
        val result = log.extractTicketCodeAndRemoveFromComment()

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                timeProvider,
                code = "DEV-123",
                comment = "Some other random comment"
            )
        )
    }
}
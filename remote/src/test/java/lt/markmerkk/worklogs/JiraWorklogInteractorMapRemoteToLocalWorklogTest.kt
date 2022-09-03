package lt.markmerkk.worklogs

import lt.markmerkk.JiraMocks
import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketCode
import org.assertj.core.api.Assertions
import org.joda.time.Duration
import org.junit.Assert.*
import org.junit.Test

class JiraWorklogInteractorMapRemoteToLocalWorklogTest {

    private val timeProvider = TimeProviderTest()
    private val now = timeProvider.now()

    @Test
    fun valid() {
        // Assemble
        val inputTicket = Mocks.createTicket(
            code = "DEV-123",
        )

        // Act
        val result = JiraWorklogInteractor.mapRemoteToLocalWorklog(
            timeProvider = timeProvider,
            fetchTime = now,
            ticket = inputTicket,
            remoteWorkLog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                started = now,
                comment = "comment1",
                durationTimeSpent = Duration.standardMinutes(10),
            )
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                id = 0,
                timeProvider = timeProvider,
                code = "DEV-123",
                comment = "comment1",
                author = "name",
                remoteData = Mocks.createRemoteData(
                    timeProvider = timeProvider,
                    remoteId = 73051,
                    url = "https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051",
                )
            )
        )
    }

    @Test
    fun noComment() {
        // Assemble
        val inputTicket = Mocks.createTicket(
            code = "DEV-123",
        )

        // Act
        val result = JiraWorklogInteractor.mapRemoteToLocalWorklog(
            timeProvider = timeProvider,
            fetchTime = now,
            ticket = inputTicket,
            remoteWorkLog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                started = now,
                comment = "",
                durationTimeSpent = Duration.standardMinutes(10),
            )
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                id = 0,
                timeProvider = timeProvider,
                code = "DEV-123",
                comment = "",
                author = "name",
                remoteData = Mocks.createRemoteData(
                    timeProvider = timeProvider,
                    remoteId = 73051,
                    url = "https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051",
                )
            )
        )
    }

    @Test
    fun nullComment() {
        // Assemble
        val inputTicket = Mocks.createTicket(
            code = "DEV-123",
        )

        // Act
        val result = JiraWorklogInteractor.mapRemoteToLocalWorklog(
            timeProvider = timeProvider,
            fetchTime = now,
            ticket = inputTicket,
            remoteWorkLog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                started = now,
                comment = null,
                durationTimeSpent = Duration.standardMinutes(10),
            )
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                id = 0,
                timeProvider = timeProvider,
                code = "DEV-123",
                comment = "",
                author = "name",
                remoteData = Mocks.createRemoteData(
                    timeProvider = timeProvider,
                    remoteId = 73051,
                    url = "https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051",
                )
            )
        )
    }

    @Test
    fun nullAuthor() {
        // Assemble
        val inputTicket = Mocks.createTicket(
            code = "DEV-123",
        )

        // Act
        val result = JiraWorklogInteractor.mapRemoteToLocalWorklog(
            timeProvider = timeProvider,
            fetchTime = now,
            ticket = inputTicket,
            remoteWorkLog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                author = null,
                started = now,
                comment = "comment1",
                durationTimeSpent = Duration.standardMinutes(10),
            )
        )

        // Assert
        Assertions.assertThat(result).isEqualTo(
            Mocks.createLog(
                id = 0,
                timeProvider = timeProvider,
                code = "DEV-123",
                comment = "comment1",
                author = "",
                remoteData = Mocks.createRemoteData(
                    timeProvider = timeProvider,
                    remoteId = 73051,
                    url = "https://jira.ito.lt/rest/api/2/issue/31463/worklog/73051",
                )
            )
        )
    }
}
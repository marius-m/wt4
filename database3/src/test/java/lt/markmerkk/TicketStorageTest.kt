package lt.markmerkk

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TicketStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class TicketStorageTest {

    private lateinit var dbDriverProvider: DBDriverProvider
    private lateinit var ticketStorage: TicketStorage

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        dbDriverProvider = DBDriverProvider(JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY))
        ticketStorage = TicketStorage(dbDriverProvider.database, timeProvider)
    }

    @After
    fun tearDown() {
        dbDriverProvider.close()
    }

    @Test
    fun insertOrUpdate_newTicket() {
        // Assemble
        val ticket = Mocks.createTicket(
                code = "DEV-111",
                description = "description",
                parentCode = "DEV-100",
                status = "To Do",
                assigneeName = "assignee",
                reporterName = "reporter",
                isWatching = true,
                remoteData = Mocks.createRemoteData(
                        timeProvider,
                        remoteId = 111,
                        fetchTime = 10L,
                        url = remoteUrl(111)
                )
        )

        // Act
        val result = ticketStorage.insertOrUpdateSync(ticket)
        val tickets = ticketStorage.loadTickets().toBlocking().value()

        // Assert
        assertThat(result).isEqualTo(1)
        assertThat(tickets).hasSize(1)
        val savedTicket = tickets.first()
        assertThat(savedTicket.id).isEqualTo(1L)
        assertThat(savedTicket.code.code).isEqualTo("DEV-111")
        assertThat(savedTicket.description).isEqualTo("description")
        assertThat(savedTicket.parentCode.code).isEqualTo("DEV-100")
        assertThat(savedTicket.status).isEqualTo("To Do")
        assertThat(savedTicket.assigneeName).isEqualTo("assignee")
        assertThat(savedTicket.reporterName).isEqualTo("reporter")
        assertThat(savedTicket.isWatching).isTrue()
        assertThat(savedTicket.remoteData!!.remoteId).isEqualTo(111L)
        assertThat(savedTicket.remoteData!!.fetchTime).isEqualTo(10L)
    }

    @Test
    fun insertOrUpdate_existingRemoteTicket_updates() {
        // Assemble
        val remoteData = Mocks.createRemoteData(timeProvider, remoteId = 111, url = remoteUrl(111))
        ticketStorage.insertOrUpdateSync(Mocks.createTicket(code = "DEV-111", status = "To Do", remoteData = remoteData))

        // Act
        ticketStorage.insertOrUpdateSync(Mocks.createTicket(code = "DEV-111", status = "Done", remoteData = remoteData))
        val tickets = ticketStorage.loadTickets().toBlocking().value()

        // Assert
        assertThat(tickets).hasSize(1)
        assertThat(tickets.first().status).isEqualTo("Done")
    }

    @Test
    fun insertOrUpdate_localTickets_alwaysInserts() {
        // Act
        ticketStorage.insertOrUpdateSync(Mocks.createTicket(code = "DEV-111"))
        ticketStorage.insertOrUpdateSync(Mocks.createTicket(code = "DEV-111"))
        val tickets = ticketStorage.loadTickets().toBlocking().value()

        // Assert
        assertThat(tickets).hasSize(2)
    }

    @Test
    fun findTicketsByCode() {
        // Assemble
        ticketStorage.insertOrUpdateSync(Mocks.createTicket(code = "DEV-111"))
        ticketStorage.insertOrUpdateSync(Mocks.createTicket(code = "DEV-222"))

        // Act
        val result = ticketStorage.findTicketsByCode("DEV-222").toBlocking().value()

        // Assert
        assertThat(result.map { it.code.code }).containsExactly("DEV-222")
    }

    @Test
    fun refreshTicketStatuses_keepsDisabledState() {
        // Assemble
        ticketStorage.refreshTicketStatusesSync(setOf("To Do", "Done"))
        ticketStorage.updateTicketStatuses(listOf(TicketStatus("Done", enabled = false)))
                .toBlocking().value()

        // Act
        ticketStorage.refreshTicketStatusesSync(setOf("To Do", "Done", "In Progress"))
        val result = ticketStorage.loadTicketStatuses().toBlocking().value()

        // Assert
        assertThat(result).containsOnly(
                TicketStatus("To Do", enabled = true),
                TicketStatus("Done", enabled = false),
                TicketStatus("In Progress", enabled = true)
        )
        assertThat(ticketStorage.enabledStatuses().toBlocking().value())
                .containsOnly("To Do", "In Progress")
    }

    @Test
    fun refreshTicketStatuses_removesUnknown() {
        // Assemble
        ticketStorage.refreshTicketStatusesSync(setOf("To Do", "Done"))

        // Act
        ticketStorage.refreshTicketStatusesSync(setOf("Done"))
        val result = ticketStorage.loadTicketStatuses().toBlocking().value()

        // Assert
        assertThat(result).containsOnly(TicketStatus("Done", enabled = true))
    }

    @Test
    fun updateTicketStatuses_ignoresUnknown() {
        // Assemble
        ticketStorage.refreshTicketStatusesSync(setOf("To Do"))

        // Act
        ticketStorage.updateTicketStatuses(listOf(TicketStatus("Unknown", enabled = false)))
                .toBlocking().value()
        val result = ticketStorage.loadTicketStatuses().toBlocking().value()

        // Assert
        assertThat(result).containsOnly(TicketStatus("To Do", enabled = true))
    }

    @Test
    fun isTicketStatusEnabled_unknownIsEnabled() {
        // Act
        val result = ticketStorage.isTicketStatusEnabled("Unknown")

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun saveTicketAsUsed_fetchRecent() {
        // Assemble
        val now = timeProvider.now()
        ticketStorage.saveTicketAsUsedSync(now, TicketCode.new("DEV-111"))
        ticketStorage.saveTicketAsUsedSync(now.plusMinutes(1), TicketCode.new("DEV-222"))
        ticketStorage.saveTicketAsUsedSync(now.plusMinutes(2), TicketCode.new("DEV-111"))
        ticketStorage.saveTicketAsUsedSync(now.plusMinutes(3), TicketCode.asEmpty())

        // Act
        val result = ticketStorage.fetchRecentTickets(limit = 10).toBlocking().value()
        val resultLimited = ticketStorage.fetchRecentTickets(limit = 1).toBlocking().value()

        // Assert
        assertThat(result.map { it.code.code }).containsExactly("DEV-111", "DEV-222")
        assertThat(result.first().lastUsed).isEqualTo(now.plusMinutes(2))
        assertThat(resultLimited.map { it.code.code }).containsExactly("DEV-111")
    }

    private fun remoteUrl(remoteId: Long): String {
        return "https://jira.example.com/rest/api/2/issue/$remoteId"
    }
}

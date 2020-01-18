package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single

class TicketStorageLoadFilteredTicketsTest {

    @Mock lateinit var connProvider: DBConnProvider
    @Mock lateinit var userSettings: UserSettings
    lateinit var ticketStorage: TicketStorage

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketStorage = spy(TicketStorage(
                connProvider = connProvider,
                timeProvider = timeProvider
        ))
        doReturn(Mocks.createJiraUser(name = "admin")).whenever(userSettings).jiraUser()
        doReturn(true).whenever(userSettings).ticketFilterIncludeAssignee
        doReturn(true).whenever(userSettings).ticketFilterIncludeReporter
        doReturn(true).whenever(userSettings).ticketFilterIncludeIsWatching
    }

    @Test
    fun noStatuses() {
        // Assemble
        val statuses = emptyList<List<String>>()
        val tickets = listOf(
                Mocks.createTicket(
                        code = "DEV-111",
                        status = "To Do",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-222",
                        status = "Done",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-333",
                        status = "Done",
                        assigneeName = "",
                        reporterName = "admin",
                        isWatching = false
                )
        )
        doReturn(Single.just(statuses)).whenever(ticketStorage).enabledStatuses()
        doReturn(Single.just(tickets)).whenever(ticketStorage).loadTickets()

        // Act
        val result = ticketStorage.loadFilteredTickets(userSettings)
                .test()

        // Assert
        result.assertValues(tickets)
    }

    @Test
    fun onlyDone() {
        // Assemble
        val statuses = listOf("Done")
        val tickets = listOf(
                Mocks.createTicket(
                        code = "DEV-111",
                        status = "To Do",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-222",
                        status = "Done",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-333",
                        status = "Done",
                        assigneeName = "",
                        reporterName = "admin",
                        isWatching = false
                )
        )
        doReturn(Single.just(statuses)).whenever(ticketStorage).enabledStatuses()
        doReturn(Single.just(tickets)).whenever(ticketStorage).loadTickets()

        // Act
        val result = ticketStorage.loadFilteredTickets(userSettings)
                .test()

        // Assert
        result.assertValues(listOf(
                tickets[1],
                tickets[2]
        ))
    }

    @Test
    fun includeOnlyAssignee() {
        // Assemble
        val statuses = emptyList<String>()
        val tickets = listOf(
                Mocks.createTicket(
                        code = "DEV-111",
                        status = "To Do",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-222",
                        status = "Done",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-333",
                        status = "Done",
                        assigneeName = "",
                        reporterName = "admin",
                        isWatching = false
                )
        )
        doReturn(Single.just(statuses)).whenever(ticketStorage).enabledStatuses()
        doReturn(Single.just(tickets)).whenever(ticketStorage).loadTickets()
        doReturn(true).whenever(userSettings).ticketFilterIncludeAssignee
        doReturn(false).whenever(userSettings).ticketFilterIncludeReporter
        doReturn(false).whenever(userSettings).ticketFilterIncludeIsWatching

        // Act
        val result = ticketStorage.loadFilteredTickets(userSettings)
                .test()

        // Assert
        result.assertValues(listOf(
                tickets[0],
                tickets[1]
        ))
    }

    @Test
    fun includeOnlyReported() {
        // Assemble
        val statuses = emptyList<String>()
        val tickets = listOf(
                Mocks.createTicket(
                        code = "DEV-111",
                        status = "To Do",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-222",
                        status = "Done",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-333",
                        status = "Done",
                        assigneeName = "",
                        reporterName = "admin",
                        isWatching = false
                )
        )
        doReturn(Single.just(statuses)).whenever(ticketStorage).enabledStatuses()
        doReturn(Single.just(tickets)).whenever(ticketStorage).loadTickets()
        doReturn(false).whenever(userSettings).ticketFilterIncludeAssignee
        doReturn(true).whenever(userSettings).ticketFilterIncludeReporter
        doReturn(false).whenever(userSettings).ticketFilterIncludeIsWatching

        // Act
        val result = ticketStorage.loadFilteredTickets(userSettings)
                .test()

        // Assert
        result.assertValues(listOf(
                tickets[0],
                tickets[1],
                tickets[2]
        ))
    }

    @Test
    fun includeOnlyWatching() {
        // Assemble
        val statuses = emptyList<String>()
        val tickets = listOf(
                Mocks.createTicket(
                        code = "DEV-111",
                        status = "To Do",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-222",
                        status = "Done",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-333",
                        status = "Done",
                        assigneeName = "",
                        reporterName = "admin",
                        isWatching = true // watchable ticket
                )
        )
        doReturn(Single.just(statuses)).whenever(ticketStorage).enabledStatuses()
        doReturn(Single.just(tickets)).whenever(ticketStorage).loadTickets()
        doReturn(false).whenever(userSettings).ticketFilterIncludeAssignee
        doReturn(false).whenever(userSettings).ticketFilterIncludeReporter
        doReturn(true).whenever(userSettings).ticketFilterIncludeIsWatching

        // Act
        val result = ticketStorage.loadFilteredTickets(userSettings)
                .test()

        // Assert
        result.assertValues(listOf(
                tickets[2]
        ))
    }

    @Test
    fun includeAllFilters() {
        // Assemble
        val statuses = emptyList<String>()
        val tickets = listOf(
                Mocks.createTicket(
                        code = "DEV-111",
                        status = "To Do",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-222",
                        status = "Done",
                        assigneeName = "admin",
                        reporterName = "admin",
                        isWatching = false
                ),
                Mocks.createTicket(
                        code = "DEV-333",
                        status = "Done",
                        assigneeName = "",
                        reporterName = "admin",
                        isWatching = false
                )
        )
        doReturn(Single.just(statuses)).whenever(ticketStorage).enabledStatuses()
        doReturn(Single.just(tickets)).whenever(ticketStorage).loadTickets()
        doReturn(true).whenever(userSettings).ticketFilterIncludeAssignee
        doReturn(true).whenever(userSettings).ticketFilterIncludeReporter
        doReturn(true).whenever(userSettings).ticketFilterIncludeIsWatching

        // Act
        val result = ticketStorage.loadFilteredTickets(userSettings)
                .test()

        // Assert
        result.assertValues(listOf(
                tickets[0],
                tickets[1],
                tickets[2]
        ))
    }
}
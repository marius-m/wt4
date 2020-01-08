package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.Mocks
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProviderJfx
import lt.markmerkk.entities.Ticket
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers

class RecentTicketLoaderFetchTest {

    @Mock lateinit var listener: RecentTicketLoader.Listener
    @Mock lateinit var ticketStorage: TicketStorage
    lateinit var recentTicketLoader: RecentTicketLoader

    private val timeProvider = TimeProviderJfx()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        recentTicketLoader = RecentTicketLoader(
                listener,
                ticketStorage,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun valid() {
        // Assemble
        val now = timeProvider.now()
        val ticketHistory = Mocks.createTicketUseHistory(timeProvider, code = "DEV-111", lastUsed = now)
        val ticket = Mocks.createTicket(code = "DEV-111", description = "description")
        doReturn(Single.just(listOf(ticketHistory))).whenever(ticketStorage).fetchRecentTickets(any())
        doReturn(Single.just(listOf(ticket))).whenever(ticketStorage).findTicketsByCode(any())

        // Act
        recentTicketLoader.fetch()

        // Assert
        val expectedHistory = Mocks.createTicketUseHistory(
                timeProvider,
                code = "DEV-111",
                description = "description",
                lastUsed = now
        )
        verify(listener).onRecentTickets(listOf(expectedHistory))
    }

    @Test
    fun noMatch() {
        // Assemble
        val now = timeProvider.now()
        val ticketHistory = Mocks.createTicketUseHistory(timeProvider, code = "DEV-111", lastUsed = now)
        doReturn(Single.just(listOf(ticketHistory))).whenever(ticketStorage).fetchRecentTickets(any())
        doReturn(Single.just(emptyList<Ticket>())).whenever(ticketStorage).findTicketsByCode(any()) // no matching ticket

        // Act
        recentTicketLoader.fetch()

        // Assert
        val expectedHistory = Mocks.createTicketUseHistory(
                timeProvider,
                code = "DEV-111",
                description = "",
                lastUsed = now
        )
        verify(listener).onRecentTickets(listOf(expectedHistory))
    }
}
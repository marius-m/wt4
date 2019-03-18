package lt.markmerkk.db2

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.tickets.TicketsNetworkRepo
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TicketsRepositoryTicketsTest {

    @Mock lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Mock lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Mock lateinit var userSettings: UserSettings
    @Mock lateinit var timeProvider: TimeProvider
    lateinit var ticketsRepository: TicketsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ticketsRepository = TicketsRepository(
                ticketsDatabaseRepo,
                ticketsNetworkRepo,
                userSettings,
                timeProvider
        )
        DateTimeUtils.setCurrentMillisFixed(1L)
        doReturn(DateTime.now()).whenever(timeProvider).now()
    }

    @Test
    fun neverFetched() {
        // Assemble
        doReturn(-1L).whenever(userSettings).ticketLastUpdate

        // Act
        val resultTickets = ticketsRepository.tickets().test()

        // Assert
        resultTickets.assertNoErrors()
        resultTickets.assertValueCount(1)
        verify(ticketsNetworkRepo).searchRemoteTicketsAndCache(any())
    }
}
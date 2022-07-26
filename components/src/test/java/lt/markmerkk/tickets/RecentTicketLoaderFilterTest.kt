package lt.markmerkk.tickets

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import lt.markmerkk.Mocks
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.entities.TicketUseHistory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers

class RecentTicketLoaderFilterTest {

    @Mock lateinit var listener: RecentTicketLoader.Listener
    @Mock lateinit var ticketStorage: TicketStorage
    lateinit var recentTicketLoader: RecentTicketLoader

    private val timeProvider = TimeProviderTest()
    private val originalTickets: List<TicketUseHistory> = listOf(
        Mocks.createTicketUseHistory(
            timeProvider = timeProvider,
            code = "DEV-111",
            description = "test description 1",
            lastUsed = timeProvider.now(),
        ),
        Mocks.createTicketUseHistory(
            timeProvider = timeProvider,
            code = "DEV-222",
            description = "test variant 2",
            lastUsed = timeProvider.now().plusMinutes(2),
        ),
        Mocks.createTicketUseHistory(
            timeProvider = timeProvider,
            code = "DEV-333",
            description = "test fixture 3",
            lastUsed = timeProvider.now().plusMinutes(5),
        ),
        Mocks.createTicketUseHistory(
            timeProvider = timeProvider,
            code = "WT-444",
            description = "test3",
            lastUsed = timeProvider.now().plusMinutes(10),
        ),
    )

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        recentTicketLoader = RecentTicketLoader(
                listener,
                ticketStorage,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
        recentTicketLoader.bindTickets(originalTickets)
    }

    @Test
    fun publishResults() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            publishResults = true,
            rawInput = "",
        )

        // Assert
        verify(listener).onRecentTickets(any())
        assertThat(result).containsAll(originalTickets)
    }

    @Test
    fun emptyFilter() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "",
        )

        // Assert
        assertThat(result).containsAll(originalTickets)
    }

    @Test
    fun hasTicketProject_upperCase() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "DEV",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-333",
                description = "test fixture 3",
                lastUsed = timeProvider.now().plusMinutes(5),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-222",
                description = "test variant 2",
                lastUsed = timeProvider.now().plusMinutes(2),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-111",
                description = "test description 1",
                lastUsed = timeProvider.now(),
            ),
        )
    }

    @Test
    fun hasTicketProject_lowercase() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "wt",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "WT-444",
                description = "test3",
                lastUsed = timeProvider.now().plusMinutes(10),
            ),
        )
    }

    @Test
    fun hasTicketNumber() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "111",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-111",
                description = "test description 1",
                lastUsed = timeProvider.now(),
            ),
        )
    }

    @Test
    fun hasTicketDescription() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "test",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "WT-444",
                description = "test3",
                lastUsed = timeProvider.now().plusMinutes(10),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-333",
                description = "test fixture 3",
                lastUsed = timeProvider.now().plusMinutes(5),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-222",
                description = "test variant 2",
                lastUsed = timeProvider.now().plusMinutes(2),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-111",
                description = "test description 1",
                lastUsed = timeProvider.now(),
            ),
        )
    }

    @Test
    fun hasTicketDescription_ignoreCase() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "TEST",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "WT-444",
                description = "test3",
                lastUsed = timeProvider.now().plusMinutes(10),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-333",
                description = "test fixture 3",
                lastUsed = timeProvider.now().plusMinutes(5),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-222",
                description = "test variant 2",
                lastUsed = timeProvider.now().plusMinutes(2),
            ),
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-111",
                description = "test description 1",
                lastUsed = timeProvider.now(),
            ),
        )
    }

    @Test
    fun hasTicketDescription2() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "var",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-222",
                description = "test variant 2",
                lastUsed = timeProvider.now().plusMinutes(2),
            ),
        )
    }

    @Test
    fun fullTicketName() {
        // Assemble
        // Act
        val result = recentTicketLoader.filterLoadedTickets(
            rawInput = "DEV-333",
        )

        // Assert
        assertThat(result).containsExactly(
            Mocks.createTicketUseHistory(
                timeProvider = timeProvider,
                code = "DEV-333",
                description = "test fixture 3",
                lastUsed = timeProvider.now().plusMinutes(5),
            ),
        )
    }
}
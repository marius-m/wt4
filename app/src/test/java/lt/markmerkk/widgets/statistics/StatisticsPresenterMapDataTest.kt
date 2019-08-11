package lt.markmerkk.widgets.statistics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.LogStorage
import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class StatisticsPresenterMapDataTest {

    @Mock lateinit var view: StatisticsContract.View
    @Mock lateinit var logStorage: LogStorage
    lateinit var presenter: StatisticsPresenter

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = StatisticsPresenter(logStorage)
        presenter.onAttach(view)
    }

    @Test
    fun valid() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-1"
                )
        )
        doReturn(inputLogs).whenever(logStorage).data

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 599999)
        )
    }

    @Test
    fun multipleDiffLogs() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-1"
                ),
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-2"
                )
        )
        doReturn(inputLogs).whenever(logStorage).data

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 599999),
                entry("DEV-2", 599999)
        )
    }

    @Test
    fun sameLogs() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-1"
                ),
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-1"
                )
        )
        doReturn(inputLogs).whenever(logStorage).data

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 1199998L)
        )
    }

    @Test
    fun containsEmptyMappings() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-1"
                ),
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        task = "DEV-1"
                ),
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(5),
                        task = ""
                ),
                Mocks.createLocalLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(4),
                        task = ""
                )
        )
        doReturn(inputLogs).whenever(logStorage).data

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 1199998L),
                entry("Not mapped", 539998L)
        )
    }
}
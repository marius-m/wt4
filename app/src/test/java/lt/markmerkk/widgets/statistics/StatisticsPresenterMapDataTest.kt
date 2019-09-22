package lt.markmerkk.widgets.statistics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.LogStorage
import lt.markmerkk.Mocks
import lt.markmerkk.TimeProviderTest
import lt.markmerkk.utils.hourglass.HourGlass
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class StatisticsPresenterMapDataTest {

    @Mock lateinit var view: StatisticsContract.View
    @Mock lateinit var logStorage: LogStorage
    @Mock lateinit var hourGlass: HourGlass
    lateinit var presenter: StatisticsPresenter

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = StatisticsPresenter(logStorage, hourGlass)
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
                entry("DEV-1", 540000)
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
                entry("DEV-1", 540000),
                entry("DEV-2", 540000)
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
                entry("DEV-1", 1080000)
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
                entry("DEV-1", 1080000),
                entry("Not mapped", 420000)
        )
    }
}
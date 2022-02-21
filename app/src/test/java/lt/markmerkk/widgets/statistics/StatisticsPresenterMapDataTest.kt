package lt.markmerkk.widgets.statistics

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.ActiveDisplayRepository
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
    @Mock lateinit var hourGlass: HourGlass
    @Mock lateinit var activeDisplayRepository: ActiveDisplayRepository
    lateinit var presenter: StatisticsPresenter

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = StatisticsPresenter(hourGlass, activeDisplayRepository)
        presenter.onAttach(view)
    }

    @Test
    fun valid() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-1"
                )
        )
        doReturn(inputLogs).whenever(activeDisplayRepository).displayLogs

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 600000)
        )
    }

    @Test
    fun multipleDiffLogs() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-1"
                ),
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-2"
                )
        )
        doReturn(inputLogs).whenever(activeDisplayRepository).displayLogs

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 600000),
                entry("DEV-2", 600000)
        )
    }

    @Test
    fun sameLogs() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-1"
                ),
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-1"
                )
        )
        doReturn(inputLogs).whenever(activeDisplayRepository).displayLogs

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 1200000L)
        )
    }

    @Test
    fun containsEmptyMappings() {
        // Assemble
        val inputLogs = listOf(
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-1"
                ),
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(10),
                        code = "DEV-1"
                ),
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(5),
                        code = ""
                ),
                Mocks.createLog(
                        timeProvider = timeProvider,
                        start = timeProvider.now(),
                        end = timeProvider.now().plusMinutes(4),
                        code = ""
                )
        )
        doReturn(inputLogs).whenever(activeDisplayRepository).displayLogs

        // Act
        val result = presenter.mapData()

        // Assert
        assertThat(result).containsExactly(
                entry("DEV-1", 1200000L),
                entry("Not mapped", 540000L)
        )
    }
}
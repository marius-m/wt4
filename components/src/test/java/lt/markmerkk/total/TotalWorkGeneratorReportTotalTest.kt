package lt.markmerkk.total

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.TimeProviderJfx
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TotalWorkGeneratorReportTotalTest {

    @Mock lateinit var hourGlass: HourGlass
    @Mock lateinit var activeDisplayRepository: ActiveDisplayRepository
    lateinit var totalWorkGenerator: TotalWorkGenerator

    private val timeProvider = TimeProviderJfx()
    private val stringRes = object : TotalWorkGenerator.StringRes {
        override fun total(total: Duration): String {
            return LogUtils.formatShortDuration(total)
        }

        override fun totalWithRunning(total: Duration, running: Duration): String {
            val formatTotal = LogUtils.formatShortDuration(total)
            val formatRunning = LogUtils.formatShortDuration(running)
            val formatResult = LogUtils.formatShortDuration(total + running)
            return "$formatTotal + $formatRunning = $formatResult"
        }

        override fun dayFinish(total: Duration): String {
            val formatTotal = LogUtils.formatShortDuration(total)
            return "Day finish ($formatTotal)"
        }

        override fun weekFinish(total: Duration): String {
            val formatTotal = LogUtils.formatShortDuration(total)
            return "Week finish ($formatTotal)"
        }
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        totalWorkGenerator = TotalWorkGenerator(
            hourGlass,
            stringRes,
            activeDisplayRepository
        )
    }

    @Test
    fun notRunning() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
                .plusDays(1)
        doReturn(false).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(activeClockDate).whenever(hourGlass).end

        // Act
        val result = totalWorkGenerator.reportTotal(
                total = totalDuration,
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("5m")
    }

    @Test
    fun runningDuration() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val runningDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
                .plusDays(1)
        doReturn(true).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(activeClockDate).whenever(hourGlass).end
        doReturn(runningDuration).whenever(hourGlass).duration

        // Act
        val result = totalWorkGenerator.reportTotal(
                total = totalDuration,
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("5m + 5m = 10m")
    }

    @Test
    fun running_displayDateBeforeCurrent() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val runningDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .minusDays(1)
                .toLocalDate()
        val displayDateEnd = displayDateStart
                .plusDays(1)
        doReturn(true).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(activeClockDate).whenever(hourGlass).end
        doReturn(runningDuration).whenever(hourGlass).duration

        // Act
        val result = totalWorkGenerator.reportTotal(
                total = totalDuration,
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("5m")
    }

    @Test
    fun running_displayDateAfterCurrent() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val runningDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .plusDays(1)
                .toLocalDate()
        val displayDateEnd = displayDateStart
                .plusDays(1)
        doReturn(true).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(activeClockDate).whenever(hourGlass).end
        doReturn(runningDuration).whenever(hourGlass).duration

        // Act
        val result = totalWorkGenerator.reportTotal(
                total = totalDuration,
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("5m")
    }
}
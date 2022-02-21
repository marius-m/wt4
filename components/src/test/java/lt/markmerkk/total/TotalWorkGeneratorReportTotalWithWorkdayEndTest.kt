package lt.markmerkk.total

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.TimeProviderJfx
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.Duration
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TotalWorkGeneratorReportTotalWithWorkdayEndTest {

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
    fun day_running() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusHours(8))
        val runningDuration = Duration(timeProvider.now(), timeProvider.now().plusMinutes(5))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
        doReturn(true).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(runningDuration).whenever(hourGlass).duration
        doReturn(totalDuration).whenever(activeDisplayRepository).totalAsDuration()
        doReturn(DisplayTypeLength.DAY).whenever(activeDisplayRepository).displayType

        // Act
        val result = totalWorkGenerator.reportTotalWithWorkdayEnd(
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("Day finish (8h)")
    }

    @Test
    fun day_notRunning() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusHours(8))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
        doReturn(false).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(totalDuration).whenever(activeDisplayRepository).totalAsDuration()
        doReturn(DisplayTypeLength.DAY).whenever(activeDisplayRepository).displayType

        // Act
        val result = totalWorkGenerator.reportTotalWithWorkdayEnd(
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("Day finish (8h)")
    }

    @Test
    fun day_unfinishedDay() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusHours(7))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
        doReturn(false).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(totalDuration).whenever(activeDisplayRepository).totalAsDuration()
        doReturn(DisplayTypeLength.DAY).whenever(activeDisplayRepository).displayType

        // Act
        val result = totalWorkGenerator.reportTotalWithWorkdayEnd(
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("7h")
    }

    @Test
    fun week_unfinishedWeek() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusHours(16))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
        doReturn(false).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(totalDuration).whenever(activeDisplayRepository).totalAsDuration()
        doReturn(DisplayTypeLength.WEEK).whenever(activeDisplayRepository).displayType

        // Act
        val result = totalWorkGenerator.reportTotalWithWorkdayEnd(
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("16h")
    }

    @Test
    fun week_finished() {
        // Assemble
        val totalDuration = Duration(timeProvider.now(), timeProvider.now().plusHours(41))
        val activeClockDate = timeProvider.now()
        val displayDateStart = timeProvider.now()
                .toLocalDate()
        val displayDateEnd = timeProvider.now()
                .toLocalDate()
        doReturn(false).whenever(hourGlass).isRunning()
        doReturn(activeClockDate).whenever(hourGlass).start
        doReturn(totalDuration).whenever(activeDisplayRepository).totalAsDuration()
        doReturn(DisplayTypeLength.WEEK).whenever(activeDisplayRepository).displayType

        // Act
        val result = totalWorkGenerator.reportTotalWithWorkdayEnd(
                displayDateStart = displayDateStart,
                displayDateEnd = displayDateEnd
        )

        // Assert
        assertThat(result).isEqualTo("Week finish (41h)")
    }

}
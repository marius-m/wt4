package lt.markmerkk.widgets.calendar

import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.paint.Color
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.Main
import lt.markmerkk.TimeProvider
import lt.markmerkk.WorklogStorage
import lt.markmerkk.timecounter.WorkGoalDurationCalculator
import lt.markmerkk.timecounter.WorkGoalReporter
import lt.markmerkk.utils.Logs.withLogInstance
import org.joda.time.Duration
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.box
import tornadofx.label
import tornadofx.pt
import tornadofx.px
import tornadofx.style
import javax.inject.Inject

class WorkForecastWidget: Fragment() {

    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var wgReporter: WorkGoalReporter
    @Inject lateinit var wgDurationCalculator: WorkGoalDurationCalculator
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var viewLabel: Label

    init {
        Main.component().inject(this)
    }

    override val root: Parent = label {
            style {
                backgroundColor.add(Color(1.0, 1.0, 1.0, 0.8))
                backgroundRadius.add(box(6.pt))
                backgroundInsets.add(box(2.px))
                fontSize = 12.0.px
                padding = box(
                    vertical = 10.px,
                    horizontal = 10.px
                )
            }
        }


    override fun onDock() {
        super.onDock()
        l.debug("onDock()".withLogInstance(this))
        this.viewLabel = root as Label
        viewLabel.text = generateReport()
    }

    override fun onUndock() {
        l.debug("onDock()".withLogInstance(this))
        super.onUndock()
    }

    fun recalculateDuration() {
        viewLabel.text = generateReport()
    }

    private fun generateReport(): String {
        val now = timeProvider.now()
        val durationLogged = wgDurationCalculator.durationLogged()
        val durationRunningClock = wgDurationCalculator.durationRunningClock(
            displayDateStart = activeDisplayRepository.displayDateRange.start,
            displayDateEnd = activeDisplayRepository.displayDateRange.endAsNextDay,
        )
        val durationWorked = wgDurationCalculator.durationWorked(
            displayDateStart = activeDisplayRepository.displayDateRange.start,
            displayDateEnd = activeDisplayRepository.displayDateRange.endAsNextDay,
        )
        val reportTotal = if (durationRunningClock == Duration.ZERO) {
            wgReporter.reportLogged(durationLogged)
        } else {
            wgReporter.reportLoggedWithOngoing(
                durationLogged = durationLogged,
                durationOngoing = durationRunningClock,
            )
        }
        return when (activeDisplayRepository.displayType) {
            DisplayTypeLength.DAY -> {
                StringBuilder()
                    .append(reportTotal)
                    .append("\n")
                    .append("\n")
                    .append(wgReporter.reportPaceDay(now = now, durationWorked = durationWorked))
                    .append("\n")
                    .append(wgReporter.reportDayShouldComplete(now = now, durationWorked = durationWorked))
                    .append("\n")
                    .append(wgReporter.reportDayGoalDuration(now = now, durationWorked = durationWorked))
                    .append("\n")
                    .append(wgReporter.reportDaySchedule(now = now))
                    .toString()
            }
            DisplayTypeLength.WEEK -> {
                StringBuilder()
                    .append(reportTotal)
                    .append("\n")
                    .append("\n")
                    .append(wgReporter.reportWeekShouldComplete(now = now, durationWorked = durationWorked))
                    .append("\n")
                    .append(wgReporter.reportPaceWeek(now = now, durationWorked = durationWorked))
                    .append("\n")
                    .append(wgReporter.reportWeekGoalDuration(now = now, durationWorked = durationWorked))
                    .toString()
            }
        }
    }

    companion object {
        val l = LoggerFactory.getLogger(WorkForecastWidget::class.java)!!
    }
}
package lt.markmerkk.total

import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.LogStorage
import lt.markmerkk.Tags
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.*
import org.slf4j.LoggerFactory

/**
 * Reports total work
 */
class TotalWorkGenerator(
        private val hourGlass: HourGlass,
        private val logStorage: LogStorage,
        private val stringRes: StringRes
) {

    /**
     * Reports total with indication workday has ended
     * @param displayDateStart start of display date
     * @param displayDateEnd end of display date. End date is always 1 day ahead
     */
    fun reportTotalWithWorkdayEnd(
            displayDateStart: LocalDate,
            displayDateEnd: LocalDate
    ): String {
        val totalDuration = logStorage.totalAsDuration()
        return when (logStorage.displayType) {
            DisplayTypeLength.DAY -> {
                if (totalDuration.standardHours >= 8) {
                    stringRes.dayFinish(totalDuration)
                } else {
                    reportTotal(totalDuration, displayDateStart, displayDateEnd)
                }
            }
            DisplayTypeLength.WEEK -> {
                if (totalDuration.standardHours >= 40) {
                    stringRes.weekFinish(totalDuration)
                } else {
                    reportTotal(totalDuration, displayDateStart, displayDateEnd)
                }
            }
        }
    }

    /**
     * Reports total including with running time
     * @param displayDateStart start of display date
     * @param displayDateEnd end of display date. End date is always 1 day ahead
     */
    fun reportTotal(
            total: Duration,
            displayDateStart: LocalDate,
            displayDateEnd: LocalDate
    ): String {
        val intStart = displayDateStart.toDateTimeAtStartOfDay()
        val intEnd = displayDateEnd.toDateTimeAtStartOfDay()
        val clockDateBetweenDisplayDate = Interval(
                intStart,
                intEnd
        ).contains(hourGlass.start)
                || hourGlass.start.toLocalDate().isEqual(displayDateStart)
        return if (hourGlass.isRunning() && clockDateBetweenDisplayDate) {
            stringRes.totalWithRunning(total, hourGlass.duration)
        } else {
            stringRes.total(total)
        }
    }

    /**
     * String resources for various states
     */
    interface StringRes {
        /**
         * Simple total message
         */
        fun total(total: Duration): String

        /**
         * Total with running time
         */
        fun totalWithRunning(total: Duration, running: Duration): String

        /**
         * Day finished message
         */
        fun dayFinish(total: Duration): String

        /**
         * Week finished message
         */
        fun weekFinish(total: Duration): String
    }

    companion object {
        val logger = LoggerFactory.getLogger(TotalWorkGenerator::class.java)!!
    }

}
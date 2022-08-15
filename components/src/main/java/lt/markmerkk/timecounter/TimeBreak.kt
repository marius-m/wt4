package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import org.joda.time.Duration
import org.joda.time.Period
import org.slf4j.LoggerFactory

/**
 * Holds time breaks without allowing to overlap time
 */
data class TimeBreak(
    val timeBreak: LocalTimeGap,
) {

    fun isEmpty(): Boolean {
        return duration() == Duration.ZERO
    }

    fun duration(): Duration = timeBreak.duration

    fun breakDurationFromTimeGap(timeWork: LocalTimeGap): Duration {
        return when {
            /**
             * -------------------------->
             *       |time gap|
             *       ----------
             *   |       break         |
             *   ----------------------
             */
            timeWork.start.isAfter(timeBreak.start) && timeWork.end.isBefore(timeBreak.end) -> {
                Period(timeWork.start, timeWork.end).toStandardDuration()
            }

            /**
             * -------------------------->
             *   |    time gap    |
             *   ------------------
             *       | break |
             *       --------
             */

            /**
             * -------------------------->
             *   | time gap |
             *   -----------
             *   | break |
             *   --------
             */

            /**
             * -------------------------->
             *   | time gap |
             *   -----------
             *     | break |
             *     --------
             */
            (timeWork.start.isBefore(timeBreak.start) || timeWork.start.isEqual(timeBreak.start)) &&
                (timeWork.end.isAfter(timeBreak.end)) -> {
                Period(timeBreak.start, timeBreak.end).toStandardDuration()
            }

            /**
             * -------------------------->
             *   | time gap |
             *   ------------
             *           | break |
             *           --------
             */
            timeWork.start.isBefore(timeBreak.start) && timeWork.end.isAfter(timeBreak.start) -> {
                Period(timeBreak.start, timeWork.end).toStandardDuration()
            }

            /**
             * -------------------------->
             *              | time gap |
             *              ------------
             *        | break |
             *        --------
             */
            timeWork.start.isAfter(timeBreak.start) && timeWork.start.isBefore(timeBreak.end) -> {
                Period(timeWork.start, timeBreak.end).toStandardDuration()
            }
            else -> Duration.ZERO
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(TimeBreak::class.java)!!

        fun asEmpty(): TimeBreak {
            return TimeBreak(timeBreak = LocalTimeGap.asEmpty())
        }

        fun asDefault(): TimeBreak {
            return TimeBreak(timeBreak = LocalTimeGap.asDefaultBreak())
        }
    }
}
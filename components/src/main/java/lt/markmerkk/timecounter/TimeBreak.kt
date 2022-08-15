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

    fun breakDurationFromTimeGap(timeGap: LocalTimeGap): Duration {
        return when {
            /**
             * -------------------------->
             *   |    time gap    |
             *   ------------------
             *       | break |
             *       --------
             */
            timeGap.start.isBefore(timeBreak.start) && timeGap.end.isAfter(timeBreak.end) -> {
                return Period(timeBreak.start, timeBreak.end).toStandardDuration()
            }

            /**
             * -------------------------->
             *   | time gap |
             *   ------------
             *           | break |
             *           --------
             */
            timeGap.start.isBefore(timeBreak.start) && timeGap.end.isAfter(timeBreak.start) -> {
                return Period(timeBreak.start, timeGap.end).toStandardDuration()
            }

            /**
             * -------------------------->
             *              | time gap |
             *              ------------
             *        | break |
             *        --------
             */
            timeGap.start.isAfter(timeBreak.start) && timeGap.start.isBefore(timeBreak.end) -> {
                return Period(timeGap.start, timeBreak.end).toStandardDuration()
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
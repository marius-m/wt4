package lt.markmerkk.timecounter

import lt.markmerkk.entities.LocalTimeGap
import lt.markmerkk.entities.ignoreOverlapping
import org.joda.time.Duration
import org.slf4j.LoggerFactory

/**
 * Holds time breaks without allowing to overlap time
 */
data class TimeBreaks private constructor(
    val breaks: List<LocalTimeGap>,
) {

    fun duration(): Duration {
        return breaks.fold(Duration.ZERO) { sum, timeGap ->
            sum.plus(timeGap.duration)
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(TimeBreaks::class.java)!!

        fun asEmpty(): TimeBreaks {
            return TimeBreaks(breaks = emptyList())
        }

        fun asDefault(): TimeBreaks {
            return TimeBreaks(breaks = listOf(LocalTimeGap.asDefaultBreak()))
        }

        /**
         * Will ignore times which have overlapping time
         */
        fun fromTimeGaps(vararg timeGaps: LocalTimeGap): TimeBreaks {
            return TimeBreaks(
                breaks = timeGaps.toList().ignoreOverlapping(),
            )
        }
    }
}
package lt.markmerkk.utils

import lt.markmerkk.entities.TimeGap
import lt.markmerkk.entities.TimeSplitPair
import org.joda.time.Duration

/**
 * Creates two separate logs from one by splitting time
 */
object LogSplitter {

    const val MIN_SPLIT_PERCENT = 1
    const val MAX_SPLIT_PERCENT = 99

    /**
     * Splits time in two amounts
     * @param splitPercent amount of time to cut off in percent, from [MIN_SPLIT_PERCENT] to [MAX_SPLIT_PERCENT]
     * @return two equally split times
     */
    fun split(
            timeGap: TimeGap,
            splitPercent: Int
    ): TimeSplitPair {
        val min = timeGap.start
        val max = timeGap.end
        if (splitPercent < MIN_SPLIT_PERCENT) {
            return TimeSplitPair(
                    first = TimeGap.from(
                            start = min,
                            end = min
                    ),
                    second = TimeGap.from(
                            start = min,
                            end = max
                    ),
                    splitPercent = MIN_SPLIT_PERCENT
            )
        }
        if (splitPercent > MAX_SPLIT_PERCENT) {
            return TimeSplitPair(
                    first = TimeGap.from(
                            start = min,
                            end = max
                    ),
                    second = TimeGap.from(
                            start = max,
                            end = max
                    ),
                    splitPercent = MAX_SPLIT_PERCENT
            )
        }
        val minutes = Duration(min, max)
                .toStandardMinutes()
                .minutes
        val splitMinutes = minutes * splitPercent / 100
        val betweenTime = min.plusMinutes(splitMinutes)
        return TimeSplitPair(
                first = TimeGap.from(
                        start = min,
                        end = betweenTime
                ),
                second = TimeGap.from(
                        start = betweenTime,
                        end = max
                ),
                splitPercent = splitPercent
        )
    }

}
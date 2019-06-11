package lt.markmerkk.entities

import org.joda.time.DateTime

data class TimeGap private constructor(
        val start: DateTime,
        val end: DateTime
) {
    companion object {
        /**
         * Ensures the time gap is a valid one
         * Note: Will always have at least 1 min gap
         */
        fun from(start: DateTime, end: DateTime): TimeGap {
            if (start.isEqual(end)
                    || end.isBefore(start)) {
                return TimeGap(start, start)
            }
            return TimeGap(start, end)
        }
    }
}
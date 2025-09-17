package lt.markmerkk

import org.joda.time.DurationFieldType
import org.joda.time.Period
import org.joda.time.PeriodType

/**
 * Created by mariusmerkevicius on 1/30/16.
 * A set of utils static methods help set [WorkLog]
 */
object WorklogUtils {
    /**
     * Formats duration time into pretty string format
     * Does not output seconds
     *
     * @param durationInSeconds provided duration to format
     * @return formatted duration
     */
    fun formatDurationFromSeconds(durationInSeconds: Long): String {
        if (durationInSeconds < 60) return "0m"
        val builder = StringBuilder()
        val type = PeriodType.forFields(
            arrayOf<DurationFieldType>(
                DurationFieldType.hours(),
                DurationFieldType.minutes()
            )
        )

        val period = Period(1000 * durationInSeconds, type)
        if (period.getHours() != 0) builder.append(period.getHours()).append("h").append(" ")
        if (period.getMinutes() != 0) builder.append(period.getMinutes()).append("m").append(" ")
        if ((builder.length > 0) && builder.get(builder.length - 1) == " ".get(0)) builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }
}

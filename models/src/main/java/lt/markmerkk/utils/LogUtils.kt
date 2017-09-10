package lt.markmerkk.utils

import org.joda.time.DurationFieldType
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.regex.Pattern

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
object LogUtils {

    const val NO_NUMBER = -1
    const val SEPERATOR = "-"

    /**
     * Inspects id for a valid type
     * @param message
     */
    fun validateTaskTitle(message: String): String {
        if (message.isEmpty()) return ""
        message.replace("\\n".toRegex(), "")
        val pattern = Pattern.compile("[a-zA-Z]+(-)?[0-9]+")
        val matcher = pattern.matcher(message.trim { it <= ' ' })
        if (matcher.find()) {
            var found = matcher.group()
            found = found.toUpperCase()
            found = found.trim { it <= ' ' }
            if (!found.contains(SEPERATOR))
                found = insertTaskSeperator(found)
            if (found.isEmpty())
                return ""
            return found
        }
        return ""
    }

    /**
     * Insers a missing seperator if it is missing.
     * @param message message that should be altered
     * *
     * @return altered message with seperator attached to its proper spot.
     */
    fun insertTaskSeperator(message: String?): String? {
        var message: String = message ?: return null
        val pattern = Pattern.compile("[a-zA-Z]+[^0-9]")
        val matcher = pattern.matcher(message.trim { it <= ' ' })
        if (matcher.find()) {
            val prefix = message.substring(0, matcher.end())
            val postfix = message.substring(matcher.end(), message.length)
            message = prefix + SEPERATOR + postfix
        }
        return message
    }

    /**
     * Splits task title and returns it
     */
    fun splitTaskTitle(message: String): String {
        if (message.isEmpty()) return ""
        message.replace("\\n".toRegex(), "")
        val pattern = Pattern.compile("[a-zA-Z]+")
        val matcher = pattern.matcher(message.trim { it <= ' ' })
        if (matcher.find()) {
            var found = matcher.group()
            found = found.toUpperCase()
            found = found.trim { it <= ' ' }
            if (found.length == 0) return ""
            return found
        }
        return ""
    }

    /**
     * Splits task title into
     */
    fun splitTaskNumber(message: String): Int {
        val validTaskTitle = validateTaskTitle(message)
        if (message.isEmpty()) return NO_NUMBER
        val pattern = Pattern.compile("[0-9]+")
        val matcher = pattern.matcher(validTaskTitle)
        if (matcher.find()) {
            val found = matcher.group()
            if (!found.isEmpty()) {
                return Integer.parseInt(found)
            }
        }
        return NO_NUMBER
    }

    /**
     * Formats duration time into pretty string format
     * @param durationMillis provided duration to format
     * *
     * @return formatted duration
     */
    fun formatDuration(durationMillis: Long): String {
        if (durationMillis < 1000)
            return "0s"
        val builder = StringBuilder()
        val type = PeriodType.forFields(arrayOf(DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds()))

        val period = Period(durationMillis, type)
        if (period.days != 0)
            builder.append(period.days).append("d").append(" ")
        if (period.hours != 0)
            builder.append(period.hours).append("h").append(" ")
        if (period.minutes != 0)
            builder.append(period.minutes).append("m").append(" ")
        if (period.seconds != 0)
            builder.append(period.seconds).append("s").append(" ")
        if (builder.length > 0 && builder[builder.length - 1] == " "[0])
            builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

    /**
     * Formats duration time into pretty and short string format
     * @param durationMillis provided duration to format
     * *
     * @return formatted duration
     */
    // fixme : needs tests, as this code was copied from earlier project
    fun formatShortDuration(durationMillis: Long): String {
        if (durationMillis < 1000 * 60)
            return "0m"
        val builder = StringBuilder()
        val type = PeriodType.forFields(arrayOf(DurationFieldType.hours(), DurationFieldType.minutes()))

        val period = Period(durationMillis, type)
        if (period.days != 0)
            builder.append(period.days).append("d").append(" ")
        if (period.hours != 0)
            builder.append(period.hours).append("h").append(" ")
        if (period.minutes != 0)
            builder.append(period.minutes).append("m").append(" ")
        if (builder.length > 0 && builder[builder.length - 1] == " "[0])
            builder.deleteCharAt(builder.length - 1)
        return builder.toString()
    }

}
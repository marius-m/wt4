package lt.markmerkk.utils

import com.google.common.base.Strings
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by mariusmerkevicius on 2/9/16. Parses and removes first part of the string message from
 * the seperator
 */
object TimeSplit {
    val SEPERATOR = ">>"
    val VALID_MESSAGE_SEPARATORS = arrayOf(SEPERATOR)

    /**
     * Adds a stamp to the raw comment.
     * Will try to remove older stamp if found one.
     * @param rawComment
     * *
     * @return
     */
    fun addStamp(start: Long, end: Long, rawComment: String): String? {
        var rawComment = rawComment
        if (Strings.isNullOrEmpty(rawComment)) return null
        rawComment = removeStamp(rawComment) ?: "" // Will remove older comment if found one
        return String.format(
                "%s - %s " + TimeSplit.SEPERATOR + " %s",
                LogFormatters.shortFormat.print(start),
                LogFormatters.shortFormat.print(end),
                rawComment
        )
    }

    /**
     * Removes stamp from the comment if there is a seperator.

     * @param message provided comment
     * *
     * @return category part
     */
    fun removeStamp(message: String): String? {
        if (!message.contains(SEPERATOR)) return message.trim { it <= ' ' }
        return pickMessage(message, "($SEPERATOR).+$")
    }

    /**
     * Picks one part of messge, depending on provided regex

     * @param message provided comment end parse
     * *
     * @param regex provided regex end use
     * *
     * @return picked comment part
     */
    internal fun pickMessage(message: String, regex: String?): String? {
        var message = message
        if (regex == null)
            throw IllegalArgumentException("Regex cannot be null!")
        if (message.isNullOrEmpty())
            return null
        // Cleaning line breaks
        message = message.replace("\\n".toRegex(), "")
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(message.trim { it <= ' ' })
        if (matcher.find()) {
            var found = matcher.group()
            found = cleanSeparators(found, VALID_MESSAGE_SEPARATORS)
            found = found.trim { it <= ' ' }
            if (found.length == 0)
                return null
            return found
        }
        return null
    }

    /**
     * Clears all valid separators

     * @param input input comment
     * *
     * @return comment without separators
     */
    internal fun cleanSeparators(input: String, separators: Array<String>): String {
        var input = input
        for (validSeparator in separators)
            input = input.replace(validSeparator, "")
        return input
    }

}

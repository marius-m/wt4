package lt.markmerkk.utils

import org.joda.time.DateTime

/**
 * Adds / removes time gap in [Log] comment
 */
object TimedCommentStamper {
    private const val SEPERATOR = ">>"
    private val timeRegex = "[0-2][0-9]:[0-5][0-9]"
    private val regexTimeGap = "($timeRegex) - ($timeRegex) ($SEPERATOR)(.+)"
            .toRegex()

    /**
     * Adds a stamp to the raw comment
     * Will try to remove older stamp if found one.
     * @param rawComment comment message
     * @return message with time gap stamp
     */
    fun addStamp(
            start: DateTime,
            end: DateTime,
            comment: String
    ): String {
        if (comment.isEmpty()) {
            return ""
        }
        val commentNoStamp = removeStamp(comment)
        val formatStart = LogFormatters.shortFormat.print(start)
        val formatEnd = LogFormatters.shortFormat.print(end)
        return "$formatStart - $formatEnd $SEPERATOR $commentNoStamp"
    }

    /**
     * Removes stamp from the comment if there is a seperator.
     * @param message provided comment
     * *
     * @return category part
     */
    fun removeStamp(message: String): String {
        if (message.isEmpty())
            return ""
        val matchResult: List<String> = regexTimeGap
                .matchEntire(message)
                ?.groupValues ?: emptyList()
        return if (matchResult.size >= 4) {
            matchResult[4].trim()
        } else {
            message.trim()
        }
    }

}

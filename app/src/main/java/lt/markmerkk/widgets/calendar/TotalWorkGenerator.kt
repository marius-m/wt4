package lt.markmerkk.widgets.calendar

import com.vdurmont.emoji.EmojiParser
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.LogStorage
import lt.markmerkk.utils.LogUtils
import org.joda.time.DurationFieldType
import org.joda.time.Period
import org.joda.time.PeriodType

/**
 * Reports total work
 */
class TotalWorkGenerator(
        private val logStorage: LogStorage
) {

    fun generateTotalMessage(): String {
        val total = logStorage.total().toLong()
        val type = PeriodType.forFields(arrayOf(DurationFieldType.hours(), DurationFieldType.minutes()))
        val period = Period(total, type)
        val totalMessage = LogUtils.formatShortDurationMillis(total)
        return when (logStorage.displayType) {
            DisplayTypeLength.DAY -> {
                if (period.hours > 8 || period.hours == 8) {
                    EmojiParser.parseToUnicode("You have finished your day ($totalMessage)! Have a :doughnut: for you hard work!")
                } else {
                    reportTotalNormal(total)
                }
            }
            DisplayTypeLength.WEEK -> {
                if (period.hours > 40 || period.hours == 40) {
                    EmojiParser.parseToUnicode("You have finished your week ($totalMessage)! Have a great weekend :clap: :boom:!")
                } else {
                    reportTotalNormal(total)
                }
            }
        }
    }

    private fun reportTotalNormal(total: Long): String {
        val formatTotalLogged = LogUtils.formatShortDurationMillis(total)
        return "Total: $formatTotalLogged"
    }

}
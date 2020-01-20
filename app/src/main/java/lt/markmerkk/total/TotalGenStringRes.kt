package lt.markmerkk.total

import com.vdurmont.emoji.EmojiParser
import lt.markmerkk.utils.LogUtils
import org.joda.time.Duration

class TotalGenStringRes: TotalWorkGenerator.StringRes {

    override fun total(total: Duration): String {
        return "Total: ${LogUtils.formatShortDuration(total)}"
    }

    override fun totalWithRunning(total: Duration, running: Duration): String {
        val formatTotal = LogUtils.formatShortDuration(total)
        val formatRunning = LogUtils.formatShortDuration(running)
        val formatResult = LogUtils.formatShortDuration(total + running)
        return "Total: $formatTotal + $formatRunning = $formatResult"
    }

    override fun dayFinish(total: Duration): String {
        val formatTotal = LogUtils.formatShortDuration(total)
        return EmojiParser.parseToUnicode("You have finished your day ($formatTotal)! Have a :doughnut: for you hard work!")
    }

    override fun weekFinish(total: Duration): String {
        val formatTotal = LogUtils.formatShortDuration(total)
        return EmojiParser.parseToUnicode("You have finished your week ($formatTotal)! Have a great weekend :clap: :boom:!")
    }
}
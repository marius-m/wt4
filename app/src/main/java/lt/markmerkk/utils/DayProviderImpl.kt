package lt.markmerkk.utils

import lt.markmerkk.entities.BasicLogStorage
import lt.markmerkk.ui.utils.DisplayType
import org.joda.time.DateTimeConstants

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
class DayProviderImpl(
        private val logStorage: BasicLogStorage
) : DayProvider {
    override fun startDay(): Long {
        when (logStorage.displayType) {
            DisplayType.WEEK -> {
                return logStorage.targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay().millis
            }
            else -> return logStorage.targetDate.millis
        }
    }

    override fun endDay(): Long {
        when (logStorage.displayType) {
            DisplayType.WEEK -> {
                return logStorage.targetDate.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withTimeAtStartOfDay().millis
            }
            else -> return logStorage.targetDate.plusDays(1).millis
        }
    }

}
package lt.markmerkk.utils

import lt.markmerkk.DayProvider
import lt.markmerkk.LogStorage
import lt.markmerkk.DisplayTypeLength
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

class DayProviderImpl(
        private val logStorage: LogStorage
) : DayProvider {

    override fun startAsDate(): LocalDate = start().toLocalDate()

    override fun endAsDate(): LocalDate = end().toLocalDate()

    override fun startMillis(): Long = start().millis

    override fun endMillis(): Long = end().millis

    private fun start(): DateTime {
        return when (logStorage.displayType) {
            DisplayTypeLength.WEEK -> {
                logStorage.targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
            }
            else -> logStorage.targetDate
        }
    }

    private fun end(): DateTime {
        return when (logStorage.displayType) {
            DisplayTypeLength.WEEK -> {
                logStorage.targetDate.withDayOfWeek(DateTimeConstants.SUNDAY)
                        .plusDays(1)
                        .withTimeAtStartOfDay()
            }
            else -> logStorage.targetDate
                    .plusDays(1)
        }
    }

}
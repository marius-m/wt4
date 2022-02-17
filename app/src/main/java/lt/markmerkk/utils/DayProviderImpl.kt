package lt.markmerkk.utils

import lt.markmerkk.DayProvider
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.LogRepository
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

class DayProviderImpl(
    private val logRepository: LogRepository
) : DayProvider {

    override fun startAsDate(): LocalDate = start().toLocalDate()

    override fun endAsDate(): LocalDate = end().toLocalDate()

    override fun startMillis(): Long = start().millis

    override fun endMillis(): Long = end().millis

    private fun start(): DateTime {
        return when (logRepository.displayType) {
            DisplayTypeLength.WEEK -> {
                logRepository.targetDate.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay()
            }
            else -> logRepository.targetDate
        }
    }

    private fun end(): DateTime {
        return when (logRepository.displayType) {
            DisplayTypeLength.WEEK -> {
                logRepository.targetDate.withDayOfWeek(DateTimeConstants.SUNDAY)
                        .plusDays(1)
                        .withTimeAtStartOfDay()
            }
            else -> logRepository.targetDate
                    .plusDays(1)
        }
    }

}
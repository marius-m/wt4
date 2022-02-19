package lt.markmerkk.utils

import lt.markmerkk.DayProvider
import lt.markmerkk.ActiveDisplayRepository
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class DayProviderImpl(
    private val activeDisplayRepository: ActiveDisplayRepository
) : DayProvider {

    override fun startAsDate(): LocalDate = start().toLocalDate()

    override fun endAsDate(): LocalDate = end().toLocalDate()

    override fun startMillis(): Long = start().millis

    override fun endMillis(): Long = end().millis

    private fun start(): DateTime {
        return activeDisplayRepository.displayDateRange
            .start
            .toDateTime(LocalTime.MIDNIGHT)
    }

    private fun end(): DateTime {
        return activeDisplayRepository.displayDateRange
            .end
            .plusDays(1)
            .toDateTime(LocalTime.MIDNIGHT)
    }

}
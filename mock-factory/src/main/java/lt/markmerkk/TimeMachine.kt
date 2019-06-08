package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.LocalDate
import org.joda.time.LocalTime

object TimeMachine {

    init {
        DateTimeUtils.setCurrentMillisFixed(1L)
    }

    fun now(): DateTime = DateTime.now()

    fun withDateTime(date: LocalDate, time: LocalTime): DateTime = DateTime.now()
            .withDate(date)
            .withTime(time)

}
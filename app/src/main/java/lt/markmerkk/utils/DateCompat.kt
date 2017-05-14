package lt.markmerkk.utils

import org.joda.time.DateTime
import java.time.LocalDate

object DateCompat {
    /**
     * Converts joda [DateTime] to java [LocalDate]
     */
    fun toJavaLocalDate(jodaDateTime: DateTime): LocalDate {
        val targetDateAsLocalDate = LocalDate.of(
                jodaDateTime.year,
                jodaDateTime.monthOfYear,
                jodaDateTime.dayOfMonth
        )
        return targetDateAsLocalDate
    }

    /**
     * Converts java [LocalDate] to joda [DateTime]
     */
    fun toJodaDateTime(javaLocalDate: LocalDate): DateTime {
        val target = DateTime(
                javaLocalDate.year,
                javaLocalDate.monthValue,
                javaLocalDate.dayOfMonth,
                0,
                0,
                0
        )
        return target
    }
}
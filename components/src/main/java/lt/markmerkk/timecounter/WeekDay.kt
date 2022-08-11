package lt.markmerkk.timecounter

import org.joda.time.DateTimeConstants

enum class WeekDay(val asInt: Int): Comparable<WeekDay> {
    MON(DateTimeConstants.MONDAY),
    TUE(DateTimeConstants.TUESDAY),
    WED(DateTimeConstants.WEDNESDAY),
    THU(DateTimeConstants.THURSDAY),
    FRI(DateTimeConstants.FRIDAY),
    SAT(DateTimeConstants.SATURDAY),
    SUN(DateTimeConstants.SUNDAY),
    ;

    companion object {
        fun fromInt(weekDayAsInt: Int): WeekDay {
            return when (weekDayAsInt) {
                DateTimeConstants.MONDAY -> MON
                DateTimeConstants.TUESDAY -> TUE
                DateTimeConstants.WEDNESDAY -> WED
                DateTimeConstants.THURSDAY -> THU
                DateTimeConstants.FRIDAY -> FRI
                DateTimeConstants.SATURDAY -> SAT
                DateTimeConstants.SUNDAY -> SUN
                else -> throw java.lang.IllegalArgumentException("Invalid WEEKDAY constant")
            }
        }
    }
}
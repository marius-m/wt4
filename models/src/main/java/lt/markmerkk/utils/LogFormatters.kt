package lt.markmerkk.utils

import org.joda.time.format.DateTimeFormat

object LogFormatters {
    const val TIME_SHORT_FORMAT = "HH:mm"
    const val DATE_SHORT_FORMAT = "yyyy-MM-dd"
    const val DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm"

    val shortFormat = DateTimeFormat.forPattern(TIME_SHORT_FORMAT)!!
    val shortFormatDate = DateTimeFormat.forPattern(DATE_SHORT_FORMAT)!!
    val longFormat = DateTimeFormat.forPattern(DATE_LONG_FORMAT)!!
}
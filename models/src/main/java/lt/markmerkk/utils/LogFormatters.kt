package lt.markmerkk.utils

import org.joda.time.format.DateTimeFormat

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
object LogFormatters {
    private val DATE_SHORT_FORMAT = "HH:mm"
    private val DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm"

    val shortFormat = DateTimeFormat.forPattern(DATE_SHORT_FORMAT)!!
    val longFormat = DateTimeFormat.forPattern(DATE_LONG_FORMAT)!!
}
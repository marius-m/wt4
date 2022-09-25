package lt.markmerkk

import lt.markmerkk.utils.LogFormatters

class LogFormatterStringRes(private val strings: Strings) : LogFormatters.StringRes {
    override fun resTomorrow(): String = strings.getString("generic_tomorrow")
}
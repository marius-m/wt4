package lt.markmerkk

import org.joda.time.DateTime

class TimeProvider {
    fun now(): DateTime = DateTime.now()
}
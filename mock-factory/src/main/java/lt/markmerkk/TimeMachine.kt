package lt.markmerkk

import org.joda.time.DateTime
import org.joda.time.DateTimeUtils

object TimeMachine {

    init {
        DateTimeUtils.setCurrentMillisFixed(1L)
    }

    fun now(): DateTime = DateTime.now()

}
package lt.markmerkk.mvp

import lt.markmerkk.entities.Log
import lt.markmerkk.entities.TicketCode
import lt.markmerkk.entities.TimeGap

/**
 * Responsible for updating log and controlling the input view changes
 */
interface LogEditService2 {
    val timeGap: TimeGap

    fun initWithLog(log: Log)

    fun updateDateTime(timeGap: TimeGap)

    fun updateCode(code: String)

    fun updateComment(comment: String)

    fun saveEntity()

    interface Listener {
        /**
         * Refresh views with new log data
         */
        fun showDateTimeChange(timeGap: TimeGap)

        /**
         * Refresh views with new log data
         */
        fun showDuration(durationAsString: String)

        fun showComment(comment: String)

        fun showCode(ticketCode: TicketCode)

        fun showSuccess(log: Log)
    }
}
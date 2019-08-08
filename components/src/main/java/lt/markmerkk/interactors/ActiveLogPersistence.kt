package lt.markmerkk.interactors

import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.TicketCode
import org.joda.time.DateTime

/**
 * Responsible of holding data for currently running clock
 * Note: Until [SimpleLogBuilder] is used, this class does not control
 * full functionality of the persistence of the active log
 */
class ActiveLogPersistence(
        private var timeProvider: TimeProvider
) {

    var ticketCode: TicketCode = TicketCode.asEmpty()
        private set
    var comment: String = ""
        private set

    fun changeTicketCode(ticketCode: String) {
        this.ticketCode = TicketCode.new(ticketCode)
    }

    fun changeComment(comment: String) {
        this.comment = comment
    }

    fun reset() {
        this.ticketCode = TicketCode.asEmpty()
        this.comment = ""
    }

}
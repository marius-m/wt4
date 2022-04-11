package lt.markmerkk.widgets.edit

import lt.markmerkk.entities.TimeGap
import org.joda.time.DateTime

interface LogDetailsContract {
    interface View {
        fun initView(labelHeader: String)
        fun closeDetails()
        fun showDateTime(start: DateTime, end: DateTime)
        fun showTicketCode(ticket: String)
        fun showComment(comment: String)
        fun showHint1(hint: String)
        fun showHint2(hint: String)
    }
    interface Presenter {
        fun onAttach()
        fun onDetach()
        fun changeDateTime(timeGap: TimeGap)
        fun openFindTickets()
        fun changeTicketCode(ticket: String)
        fun changeComment(comment: String)
        fun save()
    }
}
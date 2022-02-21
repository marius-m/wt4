package lt.markmerkk.widgets.edit

import com.jfoenix.svg.SVGGlyph
import lt.markmerkk.entities.TimeGap
import org.joda.time.DateTime

interface LogDetailsContract {
    interface View {
        fun initView(
                labelHeader: String,
                labelButtonSave: String,
                glyphButtonSave: SVGGlyph?,
                initDateTimeStart: DateTime,
                initDateTimeEnd: DateTime,
                initTicket: String,
                initComment: String,
                enableFindTickets: Boolean,
                enableDateTimeChange: Boolean
        )
        fun closeDetails()
        fun showDateTime(start: DateTime, end: DateTime)
        fun showTicketCode(ticket: String)
        fun showComment(comment: String)
        fun showHint1(hint: String)
        fun showHint2(hint: String)
        fun enableInput()
        fun disableInput()
        fun enableSaving()
        fun disableSaving()
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun save(
            timeGap: TimeGap,
            task: String,
            comment: String
        )
        fun changeDateTime(timeGap: TimeGap)
        fun openFindTickets()
        fun changeTicketCode(ticket: String)
        fun changeComment(comment: String)
    }
}
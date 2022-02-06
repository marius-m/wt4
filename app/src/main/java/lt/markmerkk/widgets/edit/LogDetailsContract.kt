package lt.markmerkk.widgets.edit

import com.jfoenix.svg.SVGGlyph
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
                start: DateTime,
                end: DateTime,
                task: String,
                comment: String
        )
        fun changeDateTimeRaw(
            startDate: String,
            startTime: String,
            endDate: String,
            endTime: String
        )
        fun changeDateTime(start: DateTime, end: DateTime)
        fun openFindTickets()
        fun changeTicketCode(ticket: String)
        fun changeComment(comment: String)
    }
}
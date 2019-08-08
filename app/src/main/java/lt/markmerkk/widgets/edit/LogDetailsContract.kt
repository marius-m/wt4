package lt.markmerkk.widgets.edit

import com.jfoenix.svg.SVGGlyph
import org.joda.time.DateTime

interface LogDetailsContract {
    interface View {
        fun initView(
                labelHeader: String,
                labelButtonSave: String,
                glyphButtonSave: SVGGlyph,
                initDateTimeStart: DateTime,
                initDateTimeEnd: DateTime,
                enableFindTickets: Boolean,
                enableDateTimeChange: Boolean
        )
        fun close()
        fun showDateTime(start: DateTime, end: DateTime)
        fun showTicket(ticket: String)
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
        fun changeDateTime(start: DateTime, end: DateTime)
        fun openFindTickets()
    }
}
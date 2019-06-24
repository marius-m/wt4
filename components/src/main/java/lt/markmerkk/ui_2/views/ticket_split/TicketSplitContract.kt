package lt.markmerkk.ui_2.views.ticket_split

import org.joda.time.DateTime

interface TicketSplitContract {
    interface View {
        fun onWorklogInit(
                showTicket: Boolean,
                ticketLabel: String,
                originalComment: String,
                isSplitEnabled: Boolean
        )
        fun showTicketLabel()
        fun hideTicketLabel()
        fun onSplitTimeUpdate(
                start: DateTime,
                end: DateTime,
                splitGap: DateTime
        )
        fun showError(error: String)
        fun hideError()
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun changeSplitBalance(balancePercent: Int)
        fun split(
                ticketName: String,
                originalComment: String,
                newComment: String
        )
    }
}
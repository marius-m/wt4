package lt.markmerkk.ui_2.views.ticket_split

import org.joda.time.DateTime

interface TicketSplitContract {
    interface View {
        fun onSplitTimeUpdate(
                start: DateTime,
                end: DateTime,
                splitGap: DateTime
        )
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun changeSplitBalance(balancePercent: Int)
        fun split()
    }
}
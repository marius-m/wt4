package lt.markmerkk.ui_2.views.ticket_merge

interface TicketMergeContract {
    interface View
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
    }
}
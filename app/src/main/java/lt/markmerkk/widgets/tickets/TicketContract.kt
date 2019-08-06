package lt.markmerkk.widgets.tickets

interface TicketContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun onTicketUpdate(tickets: List<TicketViewModel>)
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun fetchTickets()
        fun stopFetch()
        fun loadTickets()
        fun applyFilter(filter: String)
    }
}
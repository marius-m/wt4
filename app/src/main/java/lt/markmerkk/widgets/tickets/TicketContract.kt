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
        fun fetchTickets(forceFetch: Boolean)
        fun stopFetch()
        fun loadTickets()
        fun applyFilter(filter: String)
    }
}
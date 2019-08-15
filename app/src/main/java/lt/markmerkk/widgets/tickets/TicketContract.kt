package lt.markmerkk.widgets.tickets

import rx.Observable

interface TicketContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun onTicketUpdate(tickets: List<TicketViewModel>)
        fun showInputClear()
        fun hideInputClear()
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun fetchTickets(forceFetch: Boolean, filter: String)
        fun loadTickets(filter: String)
        fun stopFetch()
        fun attachFilterStream(filterAsStream: Observable<String>)
        fun handleClearVisibility(filter: String)
    }
}
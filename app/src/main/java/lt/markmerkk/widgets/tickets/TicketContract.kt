package lt.markmerkk.widgets.tickets

import lt.markmerkk.tickets.TicketLoader
import rx.Observable

interface TicketContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun onTicketUpdate(tickets: List<TicketViewModel>)
        fun onProjectCodes(projectCodes: List<String>)
        fun showInputClear()
        fun hideInputClear()
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun defaultProjectCodes(): List<String>
        fun loadProjectCodes()
        fun fetchTickets(forceFetch: Boolean, filter: String)
        fun loadTickets(filter: String)
        fun stopFetch()
        fun attachFilterStream(filterAsStream: Observable<String>)
        fun handleClearVisibility(filter: String)
    }
}
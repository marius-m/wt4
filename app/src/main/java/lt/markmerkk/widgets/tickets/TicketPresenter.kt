package lt.markmerkk.widgets.tickets

import lt.markmerkk.SchedulerProvider
import lt.markmerkk.TicketsDatabaseRepo
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketsNetworkRepo

class TicketPresenter(
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val ticketsNetworkRepo: TicketsNetworkRepo,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings,
        private val schedulerProvider: SchedulerProvider
): TicketContract.Presenter {

    private var view: TicketContract.View? = null
    private val ticketsLoader = TicketLoader(
            listener = object : TicketLoader.Listener {
                override fun onLoadStart() {
                    view?.showProgress()
                }
                override fun onLoadFinish() {
                    view?.hideProgress()
                }
                override fun onNewTickets(tickets: List<Ticket>) { }
                override fun onTicketsAvailable(tickets: List<Ticket>) {
                    val ticketViewModels = tickets.map { TicketViewModel(it) }
                    view?.onTicketUpdate(ticketViewModels)
                }
                override fun onNoTickets() {
                    view?.onTicketUpdate(emptyList())
                }
                override fun onError(throwable: Throwable) { }
            },
            ticketsDatabaseRepo = ticketsDatabaseRepo,
            ticketsNetworkRepo = ticketsNetworkRepo,
            timeProvider = timeProvider,
            userSettings = userSettings,
            ioScheduler = schedulerProvider.io(),
            uiScheduler = schedulerProvider.ui()
    )

    override fun onAttach(view: TicketContract.View) {
        this.view = view
        ticketsLoader.onAttach()
    }

    override fun onDetach() {
        ticketsLoader.onDetach()
        this.view = null
    }

    override fun fetchTickets(forceFetch: Boolean) {
        ticketsLoader.fetchTickets(forceFetch)
    }

    override fun stopFetch() {
        ticketsLoader.stopFetch()
    }

    override fun loadTickets() {
        ticketsLoader.loadTickets()
    }

    override fun applyFilter(filter: String) {
        ticketsLoader.applyFilter(filter)
    }

}
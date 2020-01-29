package lt.markmerkk.widgets.tickets

import lt.markmerkk.*
import lt.markmerkk.entities.TicketUseHistory
import lt.markmerkk.tickets.RecentTicketLoader
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketApi
import org.slf4j.LoggerFactory
import rx.Observable

class TicketPresenter(
        private val ticketStorage: TicketStorage,
        private val ticketApi: TicketApi,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings,
        private val schedulerProvider: SchedulerProvider
): TicketContract.Presenter {

    private var view: TicketContract.View? = null
    private val ticketsLoader = TicketLoader(
            listener = object : TicketLoader.Listener {
                override fun onProjectCodes(projectCodes: List<TicketLoader.ProjectCode>) {
                    view?.onProjectCodes(projectCodes = projectCodes.map { it.name })
                }

                override fun onLoadStart() {
                    view?.showProgress()
                }
                override fun onLoadFinish() {
                    view?.hideProgress()
                }
                override fun onFoundTickets(
                        searchTerm: String,
                        searchProject: String,
                        tickets: List<TicketLoader.TicketScore>
                ) {
                    val ticketViewModels = tickets.map { TicketViewModel(it.ticket, it.filterScore) }
                    logger.debug("Publishing ${ticketViewModels.size} tickets for '$searchTerm' / '$searchProject'")
                    view?.onTicketUpdate(ticketViewModels)
                }
                override fun onNoTickets(searchTerm: String, searchProject: String) {
                    logger.debug("Publishing no tickets for '$searchTerm' / '$searchProject'")
                    view?.onTicketUpdate(emptyList())
                }
                override fun onError(throwable: Throwable) { }
            },
            ticketStorage = ticketStorage,
            ticketApi = ticketApi,
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

    override fun fetchTickets(forceFetch: Boolean, filter: String, projectCode: String) {
        ticketsLoader.fetchTickets(forceFetch, filter, TicketLoader.ProjectCode(projectCode))
    }

    override fun stopFetch() {
        ticketsLoader.stopFetch()
    }

    override fun loadTickets(filter: String, projectCode: String) {
        ticketsLoader.loadTickets(filter, TicketLoader.ProjectCode(projectCode))
    }

    override fun attachFilterStream(filterAsStream: Observable<String>) {
        ticketsLoader.changeFilterStream(filterAsStream)
    }

    override fun loadProjectCodes() {
        ticketsLoader.loadProjectCodes()
    }

    override fun defaultProjectCodes(): List<String> {
        return ticketsLoader.defaultProjectCodes()
                .map { it.name }
    }

    override fun handleClearVisibility(filter: String) {
        if (filter.isNotEmpty()) {
            view?.showInputClear()
        } else {
            view?.hideInputClear()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }
}
package lt.markmerkk.widgets.tickets

import lt.markmerkk.SchedulerProvider
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketUseHistory
import lt.markmerkk.tickets.RecentTicketLoader
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketApi
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
                override fun onFoundTickets(tickets: List<TicketLoader.TicketScore>) {
                    val ticketViewModels = tickets.map { TicketViewModel(it.ticket, it.filterScore) }
                    view?.onTicketUpdate(ticketViewModels)
                }
                override fun onNoTickets() {
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

    private val recentTicketLoader = RecentTicketLoader(
            listener = object : RecentTicketLoader.Listener {
                override fun onRecentTickets(tickets: List<TicketUseHistory>) {
                    val now = timeProvider.now()
                    val ticketsVm = tickets
                            .map { RecentTicketViewModel(now, it) }
                    view?.showRecentTickets(ticketsVm)
                }
            },
            ticketStorage = ticketStorage,
            ioScheduler = schedulerProvider.io(),
            uiScheduler = schedulerProvider.ui()
    )

    override fun onAttach(view: TicketContract.View) {
        this.view = view
        ticketsLoader.onAttach()
        recentTicketLoader.onAttach()
        recentTicketLoader.fetch()
    }

    override fun onDetach() {
        recentTicketLoader.onDetach()
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

}
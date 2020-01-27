package lt.markmerkk.widgets.tickets

import lt.markmerkk.SchedulerProvider
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.TicketStatus
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.tickets.TicketStatusesLoader
import org.slf4j.LoggerFactory
import rx.Subscription

class TicketFilterSettingsPresenter(
        private val view: TicketFilterSettingsContract.View,
        private val ticketApi: TicketApi,
        private val timeProvider: TimeProvider,
        private val ticketStorage: TicketStorage,
        private val userSettings: UserSettings,
        private val schedulerProvider: SchedulerProvider
) : TicketFilterSettingsContract.Presenter {

    private var subsUpdate: Subscription? = null
    private lateinit var ticketStatusesLoader: TicketStatusesLoader

    override fun onAttach() {
        ticketStatusesLoader = TicketStatusesLoader(
                listener = view,
                ticketApi = ticketApi,
                timeProvider = timeProvider,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        ticketStatusesLoader.onAttach()
    }

    override fun onDetach() {
        ticketStatusesLoader.onDetach()
    }

    override fun loadTicketStatuses() {
        ticketStatusesLoader.fetchTicketStatuses()
    }

    override fun saveTicketStatuses(
            ticketStatusViewModels: List<TicketStatusViewModel>,
            useOnlyCurrentUser: Boolean,
            filterIncludeAssignee: Boolean,
            filterIncludeReporter: Boolean,
            filterIncludeIsWatching: Boolean
    ) {
        val newTicketStatuses = ticketStatusViewModels
                .map { TicketStatus(it.nameProperty.get(), it.enableProperty.get()) }
        val enabledTicketStatusNames = newTicketStatuses
                .filter { it.enabled }
                .map { it.name }
        subsUpdate = ticketStorage
                .updateTicketStatuses(newTicketStatuses)
                .subscribeOn(schedulerProvider.io())
                .doOnSuccess {
                    userSettings.issueJql = TicketJQLGenerator
                            .generateJQL(enabledStatuses = enabledTicketStatusNames, onlyCurrentUser = useOnlyCurrentUser)
                    userSettings.onlyCurrentUserIssues = useOnlyCurrentUser
                    userSettings.ticketFilterIncludeAssignee = filterIncludeAssignee
                    userSettings.ticketFilterIncludeReporter = filterIncludeReporter
                    userSettings.ticketFilterIncludeIsWatching = filterIncludeIsWatching
                }
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe { view.showProgress() }
                .doAfterTerminate { view.hideProgress() }
                .subscribe({
                    view.cleanUpAndExit()
                }, {
                    view.cleanUpAndExit()
                    logger.warn("Error saving ticket filter settings", it)
                })
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TicketFilterSettingsPresenter::class.java)!!
    }
}
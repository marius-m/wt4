package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Ticket
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Single
import rx.Subscription
import java.util.concurrent.TimeUnit

/**
 * Responsible for loading tickets
 */
class TicketLoader(
        private val listener: Listener,
        private val ticketStorage: TicketStorage,
        private val ticketApi: TicketApi,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    private var inputFilterSubscription: Subscription? = null
    private var networkSubscription: Subscription? = null
    private var dbSubscription: Subscription? = null
    private var dbSubsCodes: Subscription? = null

    private var projectCode: ProjectCode = ProjectCode.asEmpty()
    private var inputFilter: String = ""
        set(value) {
            field = value
            logger.debug("Change filter to $value")
        }

    fun onAttach() { }

    fun onDetach() {
        dbSubsCodes?.unsubscribe()
        dbSubscription?.unsubscribe()
        networkSubscription?.unsubscribe()
        inputFilterSubscription?.unsubscribe()
    }

    fun fetchTickets(
            forceRefresh: Boolean = false,
            inputFilter: String = "",
            projectCode: ProjectCode = ProjectCode.asEmpty()
    ) {
        this.projectCode = projectCode
        this.inputFilter = inputFilter
        networkSubscription?.unsubscribe()
        val now = timeProvider.now()
        val isFreshEnough = isTicketFreshEnough(
                lastTimeout = DateTime(userSettings.ticketLastUpdate),
                timeoutInMinutes = TICKET_TIMEOUT_MINUTES,
                now = now
        )
        if (isFreshEnough && !forceRefresh) {
            logger.info("Ignoring ticket search, as tickets are fresh enough")
            loadTickets(inputFilter, projectCode)
            return
        }
        logger.info("Refreshing tickets")
        networkSubscription = ticketApi.searchRemoteTicketsAndCache(now)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { listener.onLoadStart() }
                .doAfterTerminate { listener.onLoadFinish() }
                .flatMap { loadTicketsAsStream(this.inputFilter, this.projectCode) }
                .subscribe({
                    userSettings.ticketLastUpdate = now.millis
                    if (it.tickets.isNotEmpty()) {
                        listener.onFoundTickets(it.searchTerm, it.searchProject, it.tickets)
                    } else {
                        listener.onNoTickets(it.searchTerm, it.searchProject)
                    }
                }, {
                    listener.onError(it)
                })
    }

    fun stopFetch() {
        networkSubscription?.unsubscribe()
    }

    fun defaultProjectCodes(): List<ProjectCode> = listOf(ProjectCode.asEmpty())

    fun loadProjectCodes() {
        dbSubscription?.unsubscribe()
        dbSubscription = ticketStorage.loadTickets()
                .map { tickets ->
                    tickets.map { ProjectCode(it.code.codeProject) }
                            .toSet()
                }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({ storedProjectCodes ->
                    val projectCodes = listOf(ProjectCode.asEmpty())
                            .plus(storedProjectCodes.toList())
                            .sortedBy { it.name }
                    listener.onProjectCodes(projectCodes)
                }, {
                    listener.onProjectCodes(listOf(ProjectCode.asEmpty()))
                })
    }

    fun loadTickets(
            inputFilter: String = "",
            projectCode: ProjectCode = ProjectCode.asEmpty()
    ) {
        this.projectCode = projectCode
        this.inputFilter = inputFilter
        logger.info("Loading tickets from database with filter: $inputFilter")
        dbSubscription?.unsubscribe()
        dbSubscription = loadTicketsAsStream(inputFilter, projectCode)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.tickets.isNotEmpty()) {
                        listener.onFoundTickets(it.searchTerm, it.searchProject, it.tickets)
                    } else {
                        listener.onNoTickets(it.searchTerm, it.searchProject)
                    }
                }, {
                    listener.onError(it)
                })
    }

    fun loadTicketsAsStream(
            inputFilter: String,
            projectCode: ProjectCode
    ): Single<TicketSearchResult> {
        return ticketStorage.loadFilteredTickets(userSettings)
                .map { tickets ->
                    if (projectCode.isEmpty) {
                        tickets
                    } else {
                        tickets.filter { it.code.codeProject == projectCode.name }
                    }
                }
                .map { filter(it, searchInput = inputFilter) }
                .map { TicketSearchResult(inputFilter, projectCode.name, it) }
    }

    fun changeFilterStream(filterChange: Observable<String>) {
        inputFilterSubscription = filterChange
                .throttleLast(FILTER_INPUT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS, ioScheduler)
                .flatMapSingle {
                    this.inputFilter = it
                    loadTicketsAsStream(it, projectCode)
                }
                .observeOn(uiScheduler)
                .subscribe { listener.onFoundTickets(it.searchTerm, it.searchProject, it.tickets) }
    }

    interface Listener {
        fun onLoadStart()
        fun onLoadFinish()
        fun onProjectCodes(projectCodes: List<ProjectCode>)
        fun onFoundTickets(searchTerm: String, searchProject: String, tickets: List<TicketScore>)
        fun onNoTickets(searchTerm: String, searchProject: String)
        fun onError(throwable: Throwable)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
        const val TICKET_TIMEOUT_MINUTES = 30
        const val FILTER_MIN_INPUT = 1
        const val FILTER_FUZZY_SCORE = 20
        const val FILTER_INPUT_THROTTLE_MILLIS = 500L

        /**
         * @return unique project codes in tickets
         */
        fun filterProjectCodes(tickets: List<Ticket>): List<String> {
            return tickets
                    .map { it.code.codeProject }
                    .toSet()
                    .toList()
        }

        /**
         * Checks if timeout has expired to fetch new tickets from
         * the network
         */
        fun isTicketFreshEnough(
                lastTimeout: DateTime,
                timeoutInMinutes: Int,
                now: DateTime
        ): Boolean {
            return DateTime(lastTimeout).plusMinutes(timeoutInMinutes)
                    .isAfter(now)
        }

        fun filter(inputTickets: List<Ticket>, searchInput: String): List<TicketScore> {
            if (searchInput.length <= FILTER_MIN_INPUT) {
                return inputTickets.map { TicketScore(it, 0) }
            }
            val ticketDescriptions = inputTickets
                    .map { it.description }
            return FuzzySearch.extractTop(searchInput, ticketDescriptions, 100)
                    .filter { it.score > FILTER_FUZZY_SCORE }
                    .map { TicketScore(inputTickets[it.index], it.score) }
        }
    }

    data class TicketSearchResult(
            val searchTerm: String,
            val searchProject: String,
            val tickets: List<TicketScore>
    )

    data class TicketScore(
            val ticket: Ticket,
            val filterScore: Int
    )

    data class ProjectCode(
            val name: String
    ) {
        val isEmpty: Boolean = name.isEmpty()

        companion object {
            fun asEmpty(): ProjectCode = ProjectCode("")
        }

    }

}

fun Ticket.withScore(
        score: Int
): TicketLoader.TicketScore = TicketLoader.TicketScore(this, score)
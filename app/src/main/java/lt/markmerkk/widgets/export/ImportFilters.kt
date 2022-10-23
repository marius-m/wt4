package lt.markmerkk.widgets.export

import lt.markmerkk.utils.Logs.withLogInstance
import org.slf4j.LoggerFactory

class ImportFilters(
    private val defaultProjectFilter: String,
) {

    var lastAction: ImportFilterAction = IFActionNoAction
        private set

    fun filter(action: ImportFilterAction): ImportFilterResultState {
        l.debug(
            "filter(action: {})".withLogInstance(this),
            action,
        )
        this.lastAction = action
        return when (action) {
            IFActionNoAction -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoTickets = false,
                    isSelectTicketFromComments = false,
                    isSelectTicketFilter = true,
                    isEnabledTicketFilter = true,
                    ticketFilter = defaultProjectFilter,
                )
            }
            IFActionNoTicketCode -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoTickets = true,
                    isSelectTicketFromComments = false,
                    isSelectTicketFilter = false,
                    isEnabledTicketFilter = false,
                    ticketFilter = defaultProjectFilter,
                )
            }
            IFActionTicketFromComments -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoTickets = false,
                    isSelectTicketFromComments = true,
                    isSelectTicketFilter = false,
                    isEnabledTicketFilter = false,
                    ticketFilter = defaultProjectFilter,
                )
            }
            is IFActionTicketProjectFilter -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoTickets = false,
                    isSelectTicketFromComments = false,
                    isSelectTicketFilter = true,
                    isEnabledTicketFilter = true,
                    ticketFilter = action.filter,
                )
            }
            IFActionTicketProjectFilterDefault -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoTickets = false,
                    isSelectTicketFromComments = false,
                    isSelectTicketFilter = true,
                    isEnabledTicketFilter = true,
                    ticketFilter = defaultProjectFilter,
                )
            }
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(ImportFilters::class.java)!!
    }
}

sealed class ImportFilterAction
object IFActionNoAction: ImportFilterAction()
object IFActionNoTicketCode: ImportFilterAction()
object IFActionTicketFromComments: ImportFilterAction()
object IFActionTicketProjectFilterDefault: ImportFilterAction()
data class IFActionTicketProjectFilter(val filter: String): ImportFilterAction()

data class ImportFilterResultState(
    val action: ImportFilterAction,
    val isSelectNoTickets: Boolean,
    val isSelectTicketFromComments: Boolean,
    val isSelectTicketFilter: Boolean,
    val isEnabledTicketFilter: Boolean,
    val ticketFilter: String,
)

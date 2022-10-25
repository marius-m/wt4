package lt.markmerkk.widgets.export

import lt.markmerkk.utils.Logs.withLogInstance
import org.slf4j.LoggerFactory

class ImportFilters {

    var lastAction: ImportFilterAction = IFActionClear
        private set

    fun filter(action: ImportFilterAction): ImportFilterResultState {
        l.debug(
            "filter(action: {})".withLogInstance(this),
            action,
        )
        this.lastAction = action
        return when (action) {
            IFActionClear -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoChanges = true,
                    isSelectNoTickets = false,
                    isSelectTicketFromComments = false,
                )
            }
            IFActionNoTicketCode -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoChanges = false,
                    isSelectNoTickets = true,
                    isSelectTicketFromComments = false,
                )
            }
            IFActionTicketFromComments -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoChanges = false,
                    isSelectNoTickets = false,
                    isSelectTicketFromComments = true,
                )
            }
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(ImportFilters::class.java)!!
    }
}

sealed class ImportFilterAction
object IFActionClear: ImportFilterAction()
object IFActionNoTicketCode: ImportFilterAction()
object IFActionTicketFromComments: ImportFilterAction()

data class ImportFilterResultState(
    val action: ImportFilterAction,
    val isSelectNoChanges: Boolean,
    val isSelectNoTickets: Boolean,
    val isSelectTicketFromComments: Boolean,
)

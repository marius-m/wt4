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
                    isSelectTicketCodeFromComments = false,
                    isSelectTicketCodeAndRemoveFromComments = false,
                )
            }
            IFActionNoTicketCode -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoChanges = false,
                    isSelectNoTickets = true,
                    isSelectTicketCodeFromComments = false,
                    isSelectTicketCodeAndRemoveFromComments = false,
                )
            }
            IFActionTicketCodeFromComments -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoChanges = false,
                    isSelectNoTickets = false,
                    isSelectTicketCodeFromComments = true,
                    isSelectTicketCodeAndRemoveFromComments = false,
                )
            }
            IFActionTicketCodeAndRemoveFromComment -> {
                ImportFilterResultState(
                    action = action,
                    isSelectNoChanges = false,
                    isSelectNoTickets = false,
                    isSelectTicketCodeFromComments = false,
                    isSelectTicketCodeAndRemoveFromComments = true,
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
object IFActionTicketCodeFromComments: ImportFilterAction()
object IFActionTicketCodeAndRemoveFromComment: ImportFilterAction()

data class ImportFilterResultState(
    val action: ImportFilterAction,
    val isSelectNoChanges: Boolean,
    val isSelectNoTickets: Boolean,
    val isSelectTicketCodeFromComments: Boolean,
    val isSelectTicketCodeAndRemoveFromComments: Boolean,
)

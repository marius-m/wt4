package lt.markmerkk.widgets.dialogs

import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Strings
import lt.markmerkk.entities.Log
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import org.slf4j.LoggerFactory
import tornadofx.UIComponent

class DialogsInternal(
    private val resultDispatcher: ResultDispatcher,
    private val strings: Strings,
): Dialogs {

    override fun showDialogConfirm(
        uiComponent: UIComponent,
        header: String,
        content: String,
        onConfirm: () -> Unit,
    ) {
        resultDispatcher.publish(
            DialogConfirmWidget.RESULT_DISPATCH_KEY_BUNDLE,
            DialogConfirmWidget.DialogBundle(
                header = strings.getString("dialog_confirm_header"),
                content = strings.getString("dialog_confirm_content_delete_worklog"),
                onConfirm = onConfirm,
            )
        )
        uiComponent.openInternalWindow<DialogConfirmWidget>(
            escapeClosesWindow = true,
        )
    }

    override fun showDialogSplitTicket(
        uiComponent: UIComponent,
        worklog: Log,
    ) {
        resultDispatcher.publish(
            TicketSplitWidget.RESULT_DISPATCH_KEY_ENTITY,
            worklog,
        )
        uiComponent.openInternalWindow<TicketSplitWidget>(
            escapeClosesWindow = true,
        )
    }

    companion object {
        private val l = LoggerFactory.getLogger(DialogsInternal::class.java)!!
    }
}
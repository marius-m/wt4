package lt.markmerkk.widgets.dialogs

import lt.markmerkk.entities.Log
import tornadofx.UIComponent

interface Dialogs {
    fun showDialogConfirm(
        uiComponent: UIComponent,
        header: String,
        content: String,
        onConfirm: () -> Unit,
    )

    fun showDialogInfo(
        uiComponent: UIComponent,
        header: String,
        content: String,
    )

    fun showDialogCustomAction(
        uiComponent: UIComponent,
        header: String,
        content: String,
        actionTitle: String,
        onAction: () -> Unit,
    )

    fun showDialogSplitTicket(
        uiComponent: UIComponent,
        worklog: Log,
    )
}
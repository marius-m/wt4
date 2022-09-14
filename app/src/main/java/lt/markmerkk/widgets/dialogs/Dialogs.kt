package lt.markmerkk.widgets.dialogs

import tornadofx.UIComponent

interface Dialogs {
    fun showDialogConfirm(
        uiComponent: UIComponent,
        header: String,
        content: String,
        onConfirm: () -> Unit,
    )
}
package lt.markmerkk.widgets.dialogs

import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Strings
import org.slf4j.LoggerFactory
import tornadofx.UIComponent

class DialogsExternal(
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
        uiComponent.find<DialogConfirmWidget>().openWindow(
            stageStyle = StageStyle.DECORATED,
            modality = Modality.APPLICATION_MODAL,
            block = false,
            resizable = true
        )
    }

    companion object {
        private val l = LoggerFactory.getLogger(DialogsExternal::class.java)!!
    }
}
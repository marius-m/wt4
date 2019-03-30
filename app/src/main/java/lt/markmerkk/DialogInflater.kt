package lt.markmerkk

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXDialog
import javafx.scene.layout.StackPane
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.DialogType.*
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui_2.ClockEditDialog
import lt.markmerkk.ui_2.LogEditDialog
import lt.markmerkk.ui_2.TicketsDialog

/**
 * Responsible for initializing dialogs
 * Lifecycle: [onAttach], [onDetach]
 */
class DialogInflater(
        private val externalSourceNode: ExternalSourceNode<StackPane>,
        private val eventBus: EventBus
) {

    fun onAttach() {
        eventBus.register(this)
    }

    fun onDetach() {
        eventBus.unregister(this)
    }

    @Subscribe
    fun eventInflateDialog(event: EventInflateDialog) {
        val dialog = when (event.type) {
            ACTIVE_CLOCK -> ClockEditDialog()
            LOG_EDIT -> LogEditDialog()
            TICKET_SEARCH -> TicketsDialog()
        }
        val jfxDialog = dialog.view as JFXDialog
        jfxDialog.show(externalSourceNode.rootNode())
        jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
    }

}
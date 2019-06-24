package lt.markmerkk

import com.airhacks.afterburner.views.FXMLView
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXDialog
import javafx.scene.layout.StackPane
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.events.DialogType
import lt.markmerkk.events.DialogType.*
import lt.markmerkk.events.EventInflateDialog
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui_2.*
import tornadofx.*

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
        when (event.type) {
            ACTIVE_CLOCK -> openDialog(ClockEditDialog())
            LOG_EDIT -> openDialog(LogEditDialog())
            TICKET_SEARCH -> openDialog(TicketsDialog())
            TICKET_SPLIT -> openDialog(TicketSplitDialog())
        }
    }

    private fun openDialog(dialog: FXMLView) {
        val jfxDialog = dialog.view as JFXDialog
        jfxDialog.show(externalSourceNode.rootNode())
        jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
    }

    private fun openDialogTornado(dialog: View) {
        val jfxDialog = dialog.root as JFXDialog
        jfxDialog.show(externalSourceNode.rootNode())
        jfxDialog.setOnDialogClosed { InjectorNoDI.forget(dialog) }
    }

}
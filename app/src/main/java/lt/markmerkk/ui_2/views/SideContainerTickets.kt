package lt.markmerkk.ui_2.views

import javafx.scene.Parent
import lt.markmerkk.AttachableWidget
import lt.markmerkk.ui_2.EmptyWidget
import lt.markmerkk.widgets.tickets.TicketBasicSearchWidget
import lt.markmerkk.widgets.tickets.TicketSideDrawerWidget
import org.slf4j.LoggerFactory
import tornadofx.*

class SideContainerTickets: View(), AttachableWidget {

    private lateinit var widgetActive: Fragment

    override val root: Parent = borderpane {
        center {
            widgetActive = find<EmptyWidget>()
            add(widgetActive)
        }
    }

    override fun onDock() {
        super.onDock()
    }

    override fun onUndock() {
        super.onUndock()
    }

    fun attach() {
        // todo temporary replacement
        val newSidePanel = find<TicketSideDrawerWidget>()
//        val newSidePanel = find<TicketBasicSearchWidget>() // todo experimental ticket search
        widgetActive
                .replaceWith(newSidePanel)
        widgetActive = newSidePanel
    }

    fun detach() {
        val newSidePanel = find<EmptyWidget>()
        widgetActive
                .replaceWith(newSidePanel)
        widgetActive = newSidePanel
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SideContainerTickets::class.java)!!
    }
}
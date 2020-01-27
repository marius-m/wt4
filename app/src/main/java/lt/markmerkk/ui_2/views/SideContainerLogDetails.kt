package lt.markmerkk.ui_2.views

import javafx.scene.Parent
import lt.markmerkk.ui_2.EmptyWidget
import lt.markmerkk.widgets.edit.LogDetailsSideDrawerWidget
import org.slf4j.LoggerFactory
import tornadofx.*

class SideContainerLogDetails: View() {

    private lateinit var widgetActive: Fragment

    override val root: Parent = borderpane {
        center {
            widgetActive = find<EmptyWidget>()
            add(widgetActive)
        }
    }

    override fun onDock() {
        super.onDock()
        logger.debug("SideContainerLogDetails:onDock")
    }

    override fun onUndock() {
        logger.debug("SideContainerLogDetails:onUndock")
        super.onUndock()
    }

    fun attach() {
        val newSidePanel = find<LogDetailsSideDrawerWidget>()
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
        private val logger = LoggerFactory.getLogger(SideContainerLogDetails::class.java)!!
    }
}
package lt.markmerkk.ui_2

import org.slf4j.LoggerFactory
import tornadofx.View

/**
 * Internal functionality being applied to all views
 * Should be kept light
 */
abstract class BaseView: View() {
    override fun onDock() {
        super.onDock()
        l.debug(".onDock(this: ${this})")
    }

    override fun onUndock() {
        l.debug(".onUndock(this: ${this})")
        super.onUndock()
    }

    companion object {
        private val l = LoggerFactory.getLogger("Base")!!
    }
}
package lt.markmerkk.widgets.edit.timepick

import org.controlsfx.control.PopOver

/**
 * Provides configuration values for time selection [PopOver] in drawer
 * Has some specific values attached to it, as drawer mechanism breaks its behavior
 */
class PopOverConfigDrawerTimeSelect(
    override val title: String,
): PopOverConfig {
    override val defaultWidth: Double = 90.0
    override val defaultHeight: Double = 200.0

    override val arrowLocation: PopOver.ArrowLocation = PopOver.ArrowLocation.TOP_RIGHT
    override val isCloseButtonEnabled: Boolean = true
    override val isHeaderAlwaysVisible: Boolean = true
    override val arrowSize: Double = 6.0
    override val cornerRadius: Double = 0.0

    /**
     * If true it will break mechanism of Drawer
     * It expands randomly when trying to hide
     */
    override val isAutoHide: Boolean = false

    /**
     * If true it breaks mechanism of Drawer
     * If scrolled with ListView inside the container it
     * will randomly jump and throw exception
     */
    override val isDetachable: Boolean = false
}
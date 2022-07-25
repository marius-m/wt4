package lt.markmerkk.widgets.edit.timepick

import org.controlsfx.control.PopOver

interface PopOverConfig {
    val defaultWidth: Double
    val defaultHeight: Double

    val title: String
    val arrowLocation: PopOver.ArrowLocation
    val isCloseButtonEnabled: Boolean
    val isHeaderAlwaysVisible: Boolean
    val arrowSize: Double
    val cornerRadius: Double
    val isAutoHide: Boolean
    val isDetachable: Boolean

    fun applyValues(popOver: PopOver) {
        popOver.title = title
        popOver.arrowLocation = arrowLocation
        popOver.isCloseButtonEnabled = isCloseButtonEnabled
        popOver.isHeaderAlwaysVisible = isHeaderAlwaysVisible
        popOver.arrowSize = arrowSize
        popOver.cornerRadius = cornerRadius
        popOver.isAutoHide = isAutoHide
        popOver.isDetachable = isDetachable
    }
}
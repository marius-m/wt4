package lt.markmerkk.widgets.edit.timepick

import org.controlsfx.control.PopOver

class PopOverConfigHelp(
    override val title: String = "",
    override val arrowLocation: PopOver.ArrowLocation = PopOver.ArrowLocation.TOP_RIGHT,
): PopOverConfig {
    override val defaultWidth: Double = 90.0
    override val defaultHeight: Double = 200.0

    override val isCloseButtonEnabled: Boolean = true
    override val isHeaderAlwaysVisible: Boolean = true
    override val arrowSize: Double = 0.0
    override val cornerRadius: Double = 4.0
    override val isAutoHide: Boolean = false
    override val isDetachable: Boolean = false
}
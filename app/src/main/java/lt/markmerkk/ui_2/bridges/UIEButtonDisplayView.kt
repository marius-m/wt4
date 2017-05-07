package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.svg.SVGGlyph
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.ClockEditDialog
import lt.markmerkk.ui_2.DisplaySelectDialog

/**
 * Represents settings button, its graphics, animations
 */
class UIEButtonDisplayView(
        private val node: ExternalSourceNode<StackPane>,
        private val button: JFXButton
) : UIElement<JFXButton> {

    private val glyphView: SVGGlyph = glyph().apply { setSize(20.0, 20.0) }

    init {
        button.graphic = glyphView
        button.setOnAction {
            val jfxDialog = DisplaySelectDialog().view as JFXDialog
            jfxDialog.show(node.rootNode())
        }
    }

    override fun raw(): JFXButton = button

    override fun show() {
    }

    override fun hide() {
    }

    override fun reset() {}

    // todo : export hardcoded glyph
    private fun glyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "view",
                "M9 11.75c-.69 0-1.25.56-1.25 1.25s.56 1.25 1.25 1.25 1.25-.56 1.25-1.25-.56-1.25-1.25-1.25zm6 0c-.69 0-1.25.56-1.25 1.25s.56 1.25 1.25 1.25 1.25-.56 1.25-1.25-.56-1.25-1.25-1.25zM12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8 0-.29.02-.58.05-.86 2.36-1.05 4.23-2.98 5.21-5.37C11.07 8.33 14.05 10 17.42 10c.78 0 1.53-.09 2.25-.26.21.71.33 1.47.33 2.26 0 4.41-3.59 8-8 8z",
                Color.WHITE
        )
    }

}
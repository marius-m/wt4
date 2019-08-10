package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.svg.SVGGlyph
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.DisplayType
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.DisplaySelectDialog

/**
 * Represents settings button, its graphics, animations
 */
@Deprecated("Up for replacement")
class UIEButtonDisplayView(
        private val graphics: Graphics<SVGGlyph>,
        private val node: ExternalSourceNode<StackPane>,
        private val button: JFXButton
) : UIElement<JFXButton> {

    private val glyphView = graphics.from(Glyph.VIEW, Color.WHITE, 20.0)

    init {
        button.graphic = glyphView
        button.setOnAction {
            val fxmlView = DisplaySelectDialog(DisplayType.GRAPHS)
            val jfxDialog = fxmlView.view as JFXDialog
            jfxDialog.show(node.rootNode())
            jfxDialog.setOnDialogClosed { InjectorNoDI.forget(fxmlView) }
        }
    }

    override fun raw(): JFXButton = button

    override fun show() {
    }

    override fun hide() {
    }

    override fun reset() {}

}
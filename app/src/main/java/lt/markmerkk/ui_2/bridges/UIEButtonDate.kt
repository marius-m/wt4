package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXDialog
import com.jfoenix.skins.JFXDatePickerSkin
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Graphics
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.ClockEditDialog
import lt.markmerkk.ui_2.CurrentDayDialog

/**
 * Represents settings button, its graphics, animations
 */
class UIEButtonDate(
        private val graphics: Graphics<SVGGlyph>,
        private val node: ExternalSourceNode<StackPane>,
        private val button: JFXButton
) : UIElement<JFXButton> {

    init {
        button.graphic = graphics.glyph(
                "date",
                Color.WHITE,
                20.0
        )
        button.setOnAction {
            val jfxDialog = CurrentDayDialog().view as JFXDialog
            jfxDialog.show(node.rootNode())
            jfxDialog.setOnDialogClosed { InjectorNoDI.forget(jfxDialog) }
        }
    }

    override fun raw(): JFXButton = button

    override fun show() {
    }

    override fun hide() {
    }

    override fun reset() {}

}
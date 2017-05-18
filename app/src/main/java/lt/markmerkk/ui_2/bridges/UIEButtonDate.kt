package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXDialog
import com.jfoenix.skins.JFXDatePickerSkin
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.ClockEditDialog
import lt.markmerkk.ui_2.CurrentDayDialog

/**
 * Represents settings button, its graphics, animations
 */
class UIEButtonDate(
        private val node: ExternalSourceNode<StackPane>,
        private val button: JFXButton
) : UIElement<JFXButton> {

    private val glyphDate: SVGGlyph = settingsGlyph().apply { setSize(20.0, 20.0) }

    init {
        button.graphic = glyphDate
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

    // todo : export hardcoded glyph
    private fun settingsGlyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "date",
                "M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z",
                Color.WHITE
        )
    }

}
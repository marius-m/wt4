package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXToggleNode
import com.jfoenix.svg.SVGGlyph
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElementText
import lt.markmerkk.utils.UIEUtils

/**
 * Represents clock jfxCommitButton, its graphics, animations
 */
class UIEButtonClock(
        private val graphics: Graphics<SVGGlyph>,
        private val node: ExternalSourceNode<StackPane>,
        private val listener: Listener,
        private val button: JFXButton,
        private val buttonSettings: JFXButton,
        private val buttonToggle: JFXToggleNode
) : UIElementText<JFXButton> {

    private val glyphClock = graphics.from(Glyph.CLOCK, Color.WHITE, 20.0)

    init {
        buttonSettings.setOnAction {
            listener.onClickClockSettings()
        }
        buttonToggle.setOnAction {
            listener.onClickClock(buttonToggle.isSelected)
        }
        buttonSettings.graphic = graphics.from(Glyph.INSERT, Color.WHITE, 10.0)
        hide()
    }

    override fun updateText(text: String) {
        button.font = Font.font(UIEUtils.fontSizeBasedOnLength(text))
        button.text = text
    }

    override fun raw(): JFXButton = button

    override fun show() {
        button.graphic = null
        buttonSettings.isVisible = true
    }

    override fun hide() {
        updateText("")
        button.graphic = glyphClock
        buttonSettings.isVisible = false
    }

    override fun reset() {}


    /**
     * Hooks functions to external source
     */
    interface Listener {
        /**
         * Triggers when clock is triggered
         */
        fun onClickClock(isSelected: Boolean)

        /**
         * Triggered when clicked on clock settings
         */
        fun onClickClockSettings()

    }

}
package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXToggleNode
import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color
import lt.markmerkk.ui_2.ClockEditDialog

/**
 * Represents clock jfxCommitButton, its graphics, animations
 */
class UIEButtonClock(
        private val node: ExternalSourceNode,
        private val listener: Listener,
        private val button: JFXButton,
        private val buttonSettings: JFXButton,
        private val buttonToggle: JFXToggleNode
) : UIElementText<JFXButton> {

    private val glyphSettings: SVGGlyph = settingsGlyph().apply { setSize(10.0, 10.0) }
    private val glyphClock: SVGGlyph = glyph().apply { setSize(20.0, 20.0) }

    init {
        buttonSettings.setOnAction {
            val clockEditDialog = ClockEditDialog()
            val jfxDialog = clockEditDialog.view as JFXDialog
            jfxDialog.show(node.rootNode())
            listener.onClickClockSettings()
        }

        buttonToggle.setOnAction {
            listener.onClickClock(buttonToggle.isSelected)
        }

        buttonSettings.graphic = glyphSettings
        hide()
    }

    override fun updateText(text: String) {
        button.text = text
    }

    override fun raw(): JFXButton = button

    override fun show() {
        button.graphic = null
        button.text = ""
        buttonSettings.isVisible = true
    }

    override fun hide() {
        button.graphic = glyphClock
        button.text = ""
        buttonSettings.isVisible = false
    }

    // todo : export hardcoded glyph
    private fun glyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "clock-o",
                "M512 640v-256q0-8-5.143-13.143t-13.143-5.143h-182.857q-8 0-13.143 5.143t-5.143 13.143v36.571q0 8 5.143 13.143t13.143 5.143h128v201.143q0 8 5.143 13.143t13.143 5.143h36.571q8 0 13.143-5.143t5.143-13.143zM749.714 438.857q0 84.571-41.714 156t-113.143 113.143-156 41.714-156-41.714-113.143-113.143-41.714-156 41.714-156 113.143-113.143 156-41.714 156 41.714 113.143 113.143 41.714 156zM877.714 438.857q0-119.429-58.857-220.286t-159.714-159.714-220.286-58.857-220.286 58.857-159.714 159.714-58.857 220.286 58.857 220.286 159.714 159.714 220.286 58.857 220.286-58.857 159.714-159.714 58.857-220.286z",
                Color.WHITE
        )
    }

    // todo : export hardcoded glyph
    private fun settingsGlyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "settings",
                "M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z",
                Color.WHITE
        )
    }

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
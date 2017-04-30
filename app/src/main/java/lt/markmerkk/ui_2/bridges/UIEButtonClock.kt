package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color

/**
 * Represents clock jfxCommitButton, its graphics, animations
 */
class UIEButtonClock(
        private val button: JFXButton
) : UIElement {

    init {
        val clockGlyph = glyph()
        clockGlyph.setSize(20.0, 20.0)
        button.graphic = clockGlyph
        button.text = ""
    }

    override fun show() {
        val clockGlyph = glyph()
        clockGlyph.setSize(20.0, 20.0)
        button.graphic = clockGlyph
        button.text = ""
    }

    override fun hide() {
        button.graphic = null
        button.text = "1h 30m"
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
}
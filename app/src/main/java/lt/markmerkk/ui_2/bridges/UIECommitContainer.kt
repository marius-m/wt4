package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.animation.*
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.util.Duration
import lt.markmerkk.ui.UIElement

/**
 * Represents whole commit container, its graphics and animations
 */
class UIECommitContainer(
        private val listener: Listener,
        private val button: JFXButton,
        private val textFieldTicket: JFXTextField,
        private val textFieldComment: JFXTextField,
        private val commitContainer: Region
) : UIElement<Region> {

    private val glyphSend: SVGGlyph = glyphSend().apply { setSize(20.0, 20.0) }

    init {
        commitContainer.isManaged = false
        commitContainer.isVisible = false

        button.setOnAction {
            sendAnimation()
            listener.onClickSend(textFieldTicket.text, textFieldComment.text)
        }
        button.graphic = glyphSend
    }

    override fun raw(): Region = commitContainer

    override fun show() {
        commitContainer.isManaged = true
        commitContainer.isVisible = true
        val translateTransition = TranslateTransition(Duration.millis(100.0), commitContainer)
        translateTransition.fromY = commitContainer.translateY
        translateTransition.toY = 0.0
        translateTransition.interpolator = Interpolator.EASE_IN
        translateTransition.play()
    }

    override fun hide() {
        val translateTransition = TranslateTransition(Duration.millis(100.0), commitContainer)
        translateTransition.fromY = commitContainer.translateY
        translateTransition.toY = commitContainer.height + 20
        translateTransition.interpolator = Interpolator.EASE_IN
        translateTransition.setOnFinished {
            commitContainer.isManaged = false
            commitContainer.isVisible = false
        }
        translateTransition.play()
    }

    override fun reset() {
        textFieldTicket.text = ""
        textFieldComment.text = ""
    }

    //region Animations

    /**
     * Triggers send animation
     */
    fun sendAnimation() {
        val scaleDownTimeline = Timeline()
        val scaleDownY = KeyValue(button.scaleYProperty(), 0.0, Interpolator.EASE_OUT)
        val scaleDownX = KeyValue(button.scaleXProperty(), 0.0, Interpolator.EASE_OUT)
        val scaleDownFrame = KeyFrame(Duration.millis(100.0), scaleDownY, scaleDownX)
        scaleDownTimeline.keyFrames.add(scaleDownFrame)

        val scaleUpTimelineBig = Timeline()
        val scaleUpYBig = KeyValue(button.scaleYProperty(), 1.2)
        val scaleUpXBig = KeyValue(button.scaleXProperty(), 1.2)
        val scaleUpFrameBig = KeyFrame(Duration.millis(100.0), scaleUpYBig, scaleUpXBig)
        scaleUpTimelineBig.keyFrames.add(scaleUpFrameBig)

        val scaleUpTimelineNormal = Timeline()
        val scaleUpYNormal = KeyValue(button.scaleYProperty(), 1.0, Interpolator.EASE_IN)
        val scaleUpXNormal = KeyValue(button.scaleXProperty(), 1.0, Interpolator.EASE_IN)
        val scaleUpFrameNormal = KeyFrame(Duration.millis(30.0), scaleUpYNormal, scaleUpXNormal)
        scaleUpTimelineNormal.keyFrames.add(scaleUpFrameNormal)

        val sequentialTransition = SequentialTransition(
                button,
                scaleDownTimeline,
                scaleUpTimelineBig,
                scaleUpTimelineNormal
        )
        sequentialTransition.play()
    }

    //endregion

    //region Classes

    /**
     * Hooks functions to external source
     */
    interface Listener {
        /**
         * Triggers when send is triggered
         */
        fun onClickSend(ticket: String, message: String)

    }

    //endregion

    // todo : Export hardcoded glyph
    private fun glyphSend(): SVGGlyph {
        return SVGGlyph(
                -1,
                "test",
                "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143 11.429 0 20.571 6.286z",
                Color.WHITE
        )
    }

}
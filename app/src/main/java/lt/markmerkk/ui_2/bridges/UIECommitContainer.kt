package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.animation.*
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.util.Duration
import lt.markmerkk.Graphics
import lt.markmerkk.ui.UIElement

/**
 * Represents whole commit container, its graphics and animations
 */
class UIECommitContainer(
        private val graphics: Graphics<SVGGlyph>,
        private val listener: Listener,
        private val button: JFXButton,
        private val textFieldTicket: JFXTextField,
        private val textFieldComment: JFXTextField,
        private val commitContainer: Region
) : UIElement<Region> {

    private val glyphSend = graphics.glyph("insert", Color.WHITE, 20.0)

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

}
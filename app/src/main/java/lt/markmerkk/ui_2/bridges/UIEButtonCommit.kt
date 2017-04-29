package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import javafx.animation.*
import javafx.util.Duration

/**
 * Represents commit jfxCommitButton, its graphics, animations
 */
class UIEButtonCommit(
        private val button: JFXButton
) : UIElement {

    init {
        button.setOnAction {
            sendAnimation()
        }
    }

    override fun show() {
        throw UnsupportedOperationException()
    }

    override fun hide() {
        throw UnsupportedOperationException()
    }

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

}
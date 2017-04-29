package lt.markmerkk.ui_2.bridges

import javafx.animation.Interpolator
import javafx.animation.TranslateTransition
import javafx.scene.layout.Region
import javafx.util.Duration

/**
 * Represents whole commit container, its graphics and animations
 */
class UIECommitContainer(
        private val commitContainer: Region
) : UIElement {

    init {
        commitContainer.isManaged = false
        commitContainer.isVisible = false
    }

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

}
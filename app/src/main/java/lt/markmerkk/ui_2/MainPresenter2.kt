package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXToggleNode
import com.jfoenix.svg.SVGGlyph
import javafx.animation.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.util.Duration
import java.net.URL
import java.util.*
import java.beans.EventHandler


class MainPresenter2 : Initializable {

    @FXML lateinit var button: JFXButton
    @FXML lateinit var clockButton: JFXButton
    @FXML lateinit var clockToggle: JFXToggleNode
    @FXML lateinit var inputContainer: Region
    @FXML lateinit var outputContainer: Region
    @FXML lateinit var mainContainer: BorderPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val sendGlyph = sendGlyph()
        sendGlyph.setSize(20.0, 20.0)
        button.graphic = sendGlyph
        clockToggle.setOnAction {
            if (clockToggle.isSelected) {
                showInput2()
            } else {
                hideInput2()
            }
        }
        val clockGlyph = clockGlyph()
        clockGlyph.setSize(20.0, 20.0)
        clockButton.graphic = clockGlyph
        clockButton.text = ""
    }

    fun hideInput2() {
        val translateTransition = TranslateTransition(Duration.millis(100.0), inputContainer)
        translateTransition.fromY = inputContainer.translateY
        translateTransition.toY = inputContainer.height + 20
        translateTransition.interpolator = Interpolator.EASE_IN
        translateTransition.play()

        val clockGlyph = clockGlyph()
        clockGlyph.setSize(20.0, 20.0)
        clockButton.graphic = clockGlyph
        clockButton.text = ""
    }

    fun showInput2() {
        val translateTransition = TranslateTransition(Duration.millis(100.0), inputContainer)
        translateTransition.fromY = inputContainer.translateY
        translateTransition.toY = 0.0
        translateTransition.interpolator = Interpolator.EASE_IN
        translateTransition.play()

        clockButton.graphic = null
        clockButton.text = "1h 30m"
    }

    fun sendGlyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "test",
                "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143 11.429 0 20.571 6.286z",
                Color.WHITE
        )
    }

    fun clockGlyph(): SVGGlyph {
        return SVGGlyph(
                -1,
                "clock-o",
                "M512 640v-256q0-8-5.143-13.143t-13.143-5.143h-182.857q-8 0-13.143 5.143t-5.143 13.143v36.571q0 8 5.143 13.143t13.143 5.143h128v201.143q0 8 5.143 13.143t13.143 5.143h36.571q8 0 13.143-5.143t5.143-13.143zM749.714 438.857q0 84.571-41.714 156t-113.143 113.143-156 41.714-156-41.714-113.143-113.143-41.714-156 41.714-156 113.143-113.143 156-41.714 156 41.714 113.143 113.143 41.714 156zM877.714 438.857q0-119.429-58.857-220.286t-159.714-159.714-220.286-58.857-220.286 58.857-159.714 159.714-58.857 220.286 58.857 220.286 159.714 159.714 220.286 58.857 220.286-58.857 159.714-159.714 58.857-220.286z",
                Color.WHITE
        );
    }

}
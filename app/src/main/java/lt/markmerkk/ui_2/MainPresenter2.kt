package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.paint.Color
import java.net.URL
import java.util.*

class MainPresenter2 : Initializable {

    @FXML lateinit var button: JFXButton

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        button.setPrefSize(40.0, 40.0)
        val glyph = SVGGlyph(-1, "test", "M1008 6.286q18.857 13.714 15.429 36.571l-146.286 877.714q-2.857 16.571-18.286 25.714-8 4.571-17.714 4.571-6.286 0-13.714-2.857l-258.857-105.714-138.286 168.571q-10.286 13.143-28 13.143-7.429 0-12.571-2.286-10.857-4-17.429-13.429t-6.571-20.857v-199.429l493.714-605.143-610.857 528.571-225.714-92.571q-21.143-8-22.857-31.429-1.143-22.857 18.286-33.714l950.857-548.571q8.571-5.143 18.286-5.143 11.429 0 20.571 6.286z", Color.WHITE)
        glyph.setSize(20.0, 20.0)
        button.graphic = glyph
    }
}
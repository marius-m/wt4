package lt.markmerkk.widgets

import javafx.scene.Parent
import tornadofx.*

class ClockWidget: View() {
    override val root: Parent = stackpane {
        button("12:00") {  }
    }
}
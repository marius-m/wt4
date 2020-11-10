package lt.markmerkk.widgets.edit

import javafx.scene.Parent
import org.controlsfx.control.RangeSlider
import tornadofx.*

class TimeRangeEditWidget: Fragment() {
    override val root: Parent = borderpane {
        center {
            RangeSlider(0.0, 100.0, 10.0, 90.0)
        }
    }
}
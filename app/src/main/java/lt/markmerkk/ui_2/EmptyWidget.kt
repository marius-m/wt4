package lt.markmerkk.ui_2

import javafx.scene.Parent
import tornadofx.*

class EmptyWidget: Fragment() {
    override val root: Parent = stackpane { label() }
}
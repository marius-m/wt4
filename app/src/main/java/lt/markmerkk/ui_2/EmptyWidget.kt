package lt.markmerkk.ui_2

import javafx.scene.Parent
import tornadofx.*

class EmptyWidget: BaseFragment() {
    override val root: Parent = stackpane { label() }
}
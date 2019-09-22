package lt.markmerkk.widgets.versioner

import javafx.geometry.Pos
import javafx.scene.Parent
import lt.markmerkk.Styles
import tornadofx.*

class ChangelogWidget: View() {

    override val root: Parent = borderpane {
        minWidth = 650.0
        minHeight = 450.0
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Changelog") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            textarea {  }
        }
    }
}
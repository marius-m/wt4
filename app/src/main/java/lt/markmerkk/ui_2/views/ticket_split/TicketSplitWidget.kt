package lt.markmerkk.ui_2.views.ticket_split

import com.jfoenix.controls.JFXDialog
import javafx.scene.Parent
import lt.markmerkk.Strings
import lt.markmerkk.ui_2.views.jfxButton
import tornadofx.*

class TicketSplitWidget(
        private val strings: Strings,
        private val jfxDialog: JFXDialog
): View() {

    val header: Parent = vbox {
        label(strings.getString("ticket_split_header_title")) {
            addClass("dialog-header")
        }
    }

    val actions: List<Parent> = listOf(
            jfxButton(strings.getString("general_split").toUpperCase()) {
                setOnAction { println("Hello") }
            },
            jfxButton(strings.getString("general_dismiss").toUpperCase()) {
                setOnAction { jfxDialog.close() }
            }
    )

    override val root: Parent = vbox {
        label("Hello") {  }
    }
}
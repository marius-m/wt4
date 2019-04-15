package lt.markmerkk.ui_2.views

import apple.laf.JRSUIConstants
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import tornadofx.View
import tornadofx.combobox
import tornadofx.hgrow
import tornadofx.vgrow
import javax.swing.SingleSelectionModel

class QuickEditView: View() {

    override val root: VBox = VBox()

    init {
        with(root) {
            val prefWidth = root.prefWidth
            jfxButton("-10")
            jfxButton("-1")
            jfxCombobox(SimpleStringProperty(""), listOf("MOVE", "SCALE"))
            jfxButton("+1")
                    .apply { setPrefWidth(prefWidth) }
            jfxButton("+10")
                    .apply { setPrefWidth(prefWidth) }
        }
        root.alignment = Pos.CENTER
        root.background = Background(
                BackgroundFill(
                        Paint.valueOf("white"),
                        CornerRadii(6.0, false),
                        Insets.EMPTY
                )
        )
        root.maxWidth = 100.0
        root.maxHeight = 100.0
        root.vgrow = Priority.NEVER
        root.hgrow = Priority.NEVER
    }

}
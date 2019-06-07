package lt.markmerkk.ui_2.views

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import tornadofx.View
import tornadofx.hgrow
import tornadofx.vgrow

class QuickEditView: View() {

    override val root: VBox = VBox()

    private val containerWidth = 90.0

    init {
        with(root) {
            jfxButton("-10 min")
                    .apply { setPrefWidth(containerWidth) }
            jfxButton("-1 min")
                    .apply { setPrefWidth(containerWidth) }
            jfxCombobox(SimpleStringProperty(""), listOf("MOVE", "SCALE"))
            jfxButton("+1 min")
                    .apply { setPrefWidth(containerWidth) }
            jfxButton("+10 min")
                    .apply { setPrefWidth(containerWidth) }
        }
        root.alignment = Pos.CENTER
        root.background = Background(
                BackgroundFill(
                        Paint.valueOf("white"),
                        CornerRadii(6.0, false),
                        Insets.EMPTY
                )
        )
        root.maxWidth = containerWidth
        root.maxHeight = containerWidth
        root.vgrow = Priority.NEVER
        root.hgrow = Priority.NEVER
    }

}
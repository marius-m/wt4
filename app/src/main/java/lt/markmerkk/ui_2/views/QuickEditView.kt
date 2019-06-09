package lt.markmerkk.ui_2.views

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import lt.markmerkk.LogStorage
import tornadofx.View
import tornadofx.hgrow
import tornadofx.vgrow

class QuickEditView(
        private val listener: Listener
): View() {

    override val root: VBox = VBox()

    private val containerWidth = 90.0

    init {
        with(root) {
            jfxButton("-10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.moveBackward(10) }
            jfxButton("-1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.moveBackward(1) }
            jfxCombobox(SimpleStringProperty(""), listOf("MOVE", "SCALE"))
            jfxButton("+1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.moveForward(1) }
            jfxButton("+10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.moveForward(10) }
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

    interface Listener {
        fun moveForward(minutes: Int)
        fun moveBackward(minutes: Int)
    }

}
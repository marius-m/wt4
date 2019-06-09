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

class QuickEditWidgetShrink(
        private val listener: Listener,
        private val containerWidth: Double
): View() {

    override val root: VBox = VBox()

    init {
        with(root) {
            jfxButton("-10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.shrinkFromStart(10) }
            jfxButton("-1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.shrinkFromStart(1) }
            jfxCombobox(SimpleStringProperty(""), listOf("SHRINK"))
            jfxButton("+1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.shrinkFromEnd(1) }
            jfxButton("+10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.shrinkFromEnd(10) }
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
        fun shrinkFromStart(minutes: Int)
        fun shrinkFromEnd(minutes: Int)
    }

}
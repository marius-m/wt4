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

class QuickEditWidgetExpand(
        private val listener: Listener,
        private val containerWidth: Double
): View() {

    override val root: VBox = VBox()

    init {
        with(root) {
            jfxButton("-10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.expandToStart(10) }
            jfxButton("-1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.expandToStart(1) }
            jfxCombobox(SimpleStringProperty(""), listOf("EXPAND"))
            jfxButton("+1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.expandToEnd(1) }
            jfxButton("+10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.expandToEnd(10) }
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
        fun expandToStart(minutes: Int)
        fun expandToEnd(minutes: Int)
    }

}
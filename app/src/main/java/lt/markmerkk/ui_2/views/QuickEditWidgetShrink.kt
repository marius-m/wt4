package lt.markmerkk.ui_2.views

import com.jfoenix.controls.JFXComboBox
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import tornadofx.View
import tornadofx.hgrow
import tornadofx.selectedItem
import tornadofx.vgrow

class QuickEditWidgetShrink(
        private val listener: Listener,
        private val containerWidth: Double,
        private val quickEditActions: Set<QuickEditAction>,
        private val quickEditActionChangeListener: QuickEditActionChangeListener
): View() {

    private val jfxComboBox: JFXComboBox<String>
    override val root: VBox = VBox()

    init {
        val quickEditActionsAsString = quickEditActions.map { it.name }
        with(root) {
            jfxButton("-10 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.shrinkFromStart(10) }
            jfxButton("-1 min")
                    .apply { setPrefWidth(containerWidth) }
                    .setOnAction { listener.shrinkFromStart(1) }
            jfxComboBox = jfxCombobox(SimpleStringProperty(QuickEditAction.SHRINK.name), quickEditActionsAsString)
                    .apply {
                        setOnAction {
                            val selectAction = QuickEditAction
                                    .valueOf((it.source as JFXComboBox<String>).selectedItem!!)
                            quickEditActionChangeListener.onActiveActionChange(selectAction)
                        }
                    }
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

    fun changeActiveSelection(quickEditAction: QuickEditAction) {
        jfxComboBox.selectionModel.select(quickEditAction.name)
    }

    interface Listener {
        fun shrinkFromStart(minutes: Int)
        fun shrinkFromEnd(minutes: Int)
    }

}
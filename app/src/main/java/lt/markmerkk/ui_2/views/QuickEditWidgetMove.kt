package lt.markmerkk.ui_2.views

import com.jfoenix.controls.JFXComboBox
import com.jfoenix.svg.SVGGlyph
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import tornadofx.View
import tornadofx.hgrow
import tornadofx.selectedItem
import tornadofx.vgrow

class QuickEditWidgetMove(
        private val listener: Listener,
        private val containerWidth: Double,
        private val quickEditActions: Set<QuickEditAction>,
        private val graphics: Graphics<SVGGlyph>,
        private val quickEditActionChangeListener: QuickEditActionChangeListener
): View(), QuickEditChangableAction {

    private val quickEditActionsAsString = quickEditActions.map { it.name }
    private val jfxComboBox: JFXComboBox<String>
    override val root: VBox = VBox()

    init {
        with(root) {
            jfxButton("10 min")
                    .apply {
                        setOnAction { listener.moveBackward(10) }
                        prefWidth = containerWidth
                        graphic = graphics.from(Glyph.EXPAND_LESS, Color.BLACK, 10.0, 8.0)
                    }
            jfxButton("1 min")
                    .apply {
                       setOnAction { listener.moveBackward(1) }
                        prefWidth = containerWidth
                        graphic = graphics.from(Glyph.EXPAND_LESS, Color.BLACK, 10.0, 8.0)
                    }
                    .setOnAction { listener.moveBackward(1) }
            jfxComboBox = jfxCombobox(SimpleStringProperty(QuickEditAction.MOVE.name), quickEditActionsAsString)
                    .apply {
                        setOnAction {
                            val selectAction = QuickEditAction
                                    .valueOf((it.source as JFXComboBox<String>).selectedItem!!)
                            quickEditActionChangeListener.onActiveActionChange(selectAction)
                        }
                    }
            jfxButton("+1 min")
                    .apply {
                        setOnAction { listener.moveForward(1) }
                        prefWidth = containerWidth
                        graphic = graphics.from(Glyph.EXPAND_MORE, Color.BLACK, 10.0, 8.0)
                    }
            jfxButton("+10 min")
                    .apply {
                        setOnAction { listener.moveForward(10) }
                        prefWidth = containerWidth
                        graphic = graphics.from(Glyph.EXPAND_MORE, Color.BLACK, 10.0, 8.0)
                    }
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

    override fun changeActiveAction(quickEditAction: QuickEditAction) {
        val actionIndex = quickEditActionsAsString
                .indexOf(quickEditAction.name)
        jfxComboBox.selectionModel.clearAndSelect(actionIndex)
    }

    interface Listener {
        fun moveForward(minutes: Int)
        fun moveBackward(minutes: Int)
    }

}
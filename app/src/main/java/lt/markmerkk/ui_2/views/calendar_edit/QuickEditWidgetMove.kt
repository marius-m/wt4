package lt.markmerkk.ui_2.views.calendar_edit

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
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxCombobox
import tornadofx.View
import tornadofx.hgrow
import tornadofx.selectedItem
import tornadofx.vgrow

class QuickEditWidgetMove(
        private val listener: Listener,
        private val uiPrefs: QuickEditUiPrefs,
        private val quickEditActions: Set<QuickEditAction>,
        private val graphics: Graphics<SVGGlyph>,
        private val quickEditActionChangeListener: QuickEditActionChangeListener
) : View(), QuickEditChangableAction {

    private val quickEditActionsAsString = quickEditActions.map { it.name }
    private val jfxComboBox: JFXComboBox<String>
    override val root: HBox = HBox()

    init {
        with(root) {
            jfxButton()
                    .apply {
                        setOnAction { listener.moveBackward(10) }
                        prefWidth = uiPrefs.prefWidthActionIcons
                        graphic = graphics.from(
                                Glyph.ARROW_FAST_REWIND,
                                Color.BLACK,
                                uiPrefs.widthActionIconFaster,
                                uiPrefs.heightActionIconFaster
                        )
                    }
            jfxButton()
                    .apply {
                        setOnAction { listener.moveBackward(1) }
                        prefWidth = uiPrefs.prefWidthActionIcons
                        graphic = graphics.from(
                                Glyph.ARROW_REWIND,
                                Color.BLACK,
                                uiPrefs.widthActionIcon,
                                uiPrefs.heightActionIcon
                        )
                    }
                    .setOnAction { listener.moveBackward(1) }
            jfxComboBox = jfxCombobox(SimpleStringProperty(QuickEditAction.MOVE.name), quickEditActionsAsString)
                    .apply {
                        minWidth = uiPrefs.prefWidthTypeSelector
                        prefWidth = uiPrefs.prefWidthTypeSelector
                        setOnAction {
                            val selectAction = QuickEditAction
                                    .valueOf((it.source as JFXComboBox<String>).selectedItem!!)
                            quickEditActionChangeListener.onActiveActionChange(selectAction)
                        }
                    }
            jfxButton()
                    .apply {
                        setOnAction { listener.moveForward(1) }
                        prefWidth = uiPrefs.prefWidthActionIcons
                        graphic = graphics.from(
                                Glyph.ARROW_FORWARD,
                                Color.BLACK,
                                uiPrefs.widthActionIcon,
                                uiPrefs.heightActionIcon
                        )
                    }
            jfxButton()
                    .apply {
                        setOnAction { listener.moveForward(10) }
                        prefWidth = uiPrefs.prefWidthActionIcons
                        graphic = graphics.from(
                                Glyph.ARROW_FAST_FORWARD,
                                Color.BLACK,
                                uiPrefs.widthActionIconFaster,
                                uiPrefs.heightActionIconFaster
                        )
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
        root.maxWidth = uiPrefs.maxWidthContainer
        root.maxHeight = uiPrefs.prefHeightContainer
        root.prefHeight = uiPrefs.prefHeightContainer
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
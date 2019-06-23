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
import lt.markmerkk.Tags
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxCombobox
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.hgrow
import tornadofx.selectedItem
import tornadofx.vgrow

class QuickEditWidgetScale(
        private val presenter: QuickEditContract.ScalePresenter,
        private val uiPrefs: QuickEditUiPrefs,
        private val quickEditActions: Set<QuickEditAction>,
        private val graphics: Graphics<SVGGlyph>,
        private val scaleStepMinutes: Int,
        private val quickEditActionChangeListener: QuickEditActionChangeListener
): View(),
        QuickEditChangableAction,
        QuickEditContract.ScaleView,
        QuickEditContract.SelectableView,
        QuickEditContract.LifecycleView
{

    private val quickEditActionsAsString = quickEditActions.map { it.name }
    private val jfxComboBox: JFXComboBox<String>
    override val root = HBox()

    init {
        with(root) {
            jfxButton {
                setOnAction { presenter.expandToStart(scaleStepMinutes) }
                prefWidth = uiPrefs.prefWidthActionIcons
                graphic = graphics.from(
                        Glyph.ARROW_REWIND,
                        Color.BLACK,
                        uiPrefs.widthActionIcon,
                        uiPrefs.heightActionIcon
                )
            }
            jfxButton {
                setOnAction { presenter.shrinkFromStart(scaleStepMinutes) }
                prefWidth = uiPrefs.prefWidthActionIcons
                graphic = graphics.from(
                        Glyph.ARROW_FORWARD,
                        Color.BLACK,
                        uiPrefs.widthActionIcon,
                        uiPrefs.heightActionIcon
                )
            }
            jfxComboBox = jfxCombobox(SimpleStringProperty(QuickEditAction.SCALE.name), quickEditActionsAsString) {
                minWidth = uiPrefs.prefWidthTypeSelector
                prefWidth = uiPrefs.prefWidthTypeSelector
                setOnAction {
                    val selectAction = QuickEditAction
                            .valueOf((it.source as JFXComboBox<String>).selectedItem!!)
                    quickEditActionChangeListener.onActiveActionChange(selectAction)
                }
            }
            jfxButton {
                setOnAction { presenter.shrinkFromEnd(scaleStepMinutes) }
                prefWidth = uiPrefs.prefWidthActionIcons
                graphic = graphics.from(
                        Glyph.ARROW_REWIND,
                        Color.BLACK,
                        uiPrefs.widthActionIcon,
                        uiPrefs.heightActionIcon
                )
            }
            jfxButton {
                setOnAction { presenter.expandToEnd(scaleStepMinutes) }
                prefWidth = uiPrefs.prefWidthActionIcons
                graphic = graphics.from(
                        Glyph.ARROW_FORWARD,
                        Color.BLACK,
                        uiPrefs.widthActionIcon,
                        uiPrefs.heightActionIcon
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

    override fun onAttach() {
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
    }

    override fun onSelectLog(logId: Long) {
        presenter.selectLogId(logId)
    }

    override fun changeActiveAction(quickEditAction: QuickEditAction) {
        val actionIndex = quickEditActionsAsString
                .indexOf(quickEditAction.name)
        jfxComboBox.selectionModel.clearAndSelect(actionIndex)
    }

    companion object {
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}
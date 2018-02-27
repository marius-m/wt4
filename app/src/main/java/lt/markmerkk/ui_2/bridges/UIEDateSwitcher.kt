package lt.markmerkk.ui_2.bridges

import com.airhacks.afterburner.views.FXMLView
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.Color
import lt.markmerkk.DisplayTypeLength
import lt.markmerkk.Graphics
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui.UILifecycleBridge
import lt.markmerkk.ui_2.CurrentDayDialog
import lt.markmerkk.utils.DateCompat
import lt.markmerkk.utils.DateSwittcherFormatter

/**
 * Represents date switcher element
 */
class UIEDateSwitcher(
        private val node: ExternalSourceNode<StackPane>,
        private val jfxContainerContentDateSwitcher: HBox,
        private val jfxDateSwitcherNext: JFXButton,
        private val jfxDateSwitcherPrev: JFXButton,
        private val jfxDateSwitcherDate: JFXButton,
        private val graphics: Graphics<SVGGlyph>,
        private val storage: LogStorage
) : UIElement<HBox>, UILifecycleBridge {

    override fun onAttach() {
        storage.register(dataListener)
        render()
    }

    override fun onDetach() {
        storage.unregister(dataListener)
    }

    init {
        jfxDateSwitcherPrev.graphic = graphics.glyph("arrow_left", Color.BLACK, 8.0, 12.0)
        jfxDateSwitcherNext.graphic = graphics.glyph("arrow_right", Color.BLACK, 8.0, 12.0)
        jfxDateSwitcherDate.setOnAction {
            val jfxDialog = CurrentDayDialog().view as JFXDialog
            jfxDialog.show(node.rootNode())
            jfxDialog.setOnDialogClosed { InjectorNoDI.forget(jfxDialog) }
        }
        jfxDateSwitcherPrev.setOnAction {
            when (storage.displayType) {
                DisplayTypeLength.DAY -> storage.targetDate = storage.targetDate.minusDays(1)
                DisplayTypeLength.WEEK -> storage.targetDate = storage.targetDate.minusDays(7)
            }
        }
        jfxDateSwitcherNext.setOnAction {
            when (storage.displayType) {
                DisplayTypeLength.DAY -> storage.targetDate = storage.targetDate.plusDays(1)
                DisplayTypeLength.WEEK -> storage.targetDate = storage.targetDate.plusDays(7)
            }
        }
    }

    private fun render() {
        val localDate = DateCompat.toJavaLocalDate(storage.targetDate)
        when (storage.displayType) {
            DisplayTypeLength.DAY -> jfxDateSwitcherDate.text = DateSwittcherFormatter.formatDateForDay(localDate)
            DisplayTypeLength.WEEK -> jfxDateSwitcherDate.text = DateSwittcherFormatter.formatDateForWeek(localDate)
        }
    }

    override fun raw(): HBox = jfxContainerContentDateSwitcher

    override fun show() {
        jfxContainerContentDateSwitcher.isVisible = true
        jfxContainerContentDateSwitcher.isManaged = true
    }

    override fun hide() {
        jfxContainerContentDateSwitcher.isVisible = false
        jfxContainerContentDateSwitcher.isManaged = false
    }

    override fun reset() { }

    private val dataListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            render()
        }
    }

}
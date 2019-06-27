package lt.markmerkk.ui_2.views.ticket_merge

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXSlider
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.ui_2.views.*
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import tornadofx.*

class TicketMergeWidget(
        private val strings: Strings,
        private val graphics: Graphics<SVGGlyph>,
        private val jfxDialog: JFXDialog,
        private val presenter: TicketMergeContract.Presenter
) : View(),
        TicketMergeContract.View,
        LifecycleView
{
    val header: Parent = vbox {
        label(strings.getString("ticket_merge_header_title")) {
            addClass("dialog-header")
        }
    }

    private val actionSplit = jfxButton(strings.getString("general_split").toUpperCase()) {
        graphic = graphics.from(Glyph.SPLIT, Color.BLACK, size = 12.0)
        setOnAction {
            jfxDialog.close()
        }
    }

    val actions: List<Parent> = listOf(
            actionSplit,
            jfxButton(strings.getString("general_dismiss").toUpperCase()) {
                setOnAction { jfxDialog.close() }
            }
    )

    override val root: Parent = vbox {
        stackpane {
            vgrow = Priority.ALWAYS
            calendarFxDetailedDay()
        }
    }

    override fun onAttach() {
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKET_MERGE)
    }

}
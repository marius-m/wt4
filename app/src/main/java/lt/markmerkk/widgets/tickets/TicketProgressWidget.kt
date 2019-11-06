package lt.markmerkk.widgets.tickets

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSpinner
import tornadofx.*
import javax.inject.Inject

class TicketProgressWidget: Fragment() {

    @Inject lateinit var graphics: Graphics<SVGGlyph>

    lateinit var viewProgress: JFXSpinner
    lateinit var viewButtonRefresh: JFXButton
    lateinit var viewButtonStop: JFXButton

    init {
        Main.component().inject(this)
    }

    override val root: Parent = stackpane {
        minWidth = 24.0
        minHeight = 24.0
        prefWidth = 24.0
        prefHeight = 24.0
        maxWidth = 24.0
        maxHeight = 24.0
        style {
            padding = box(2.px)
        }

        viewProgress = jfxSpinner {  }
        viewButtonRefresh = jfxButton {
            graphic = graphics.from(Glyph.REFRESH2, Color.BLACK, 12.0)
        }
        viewButtonStop = jfxButton {
            graphic = graphics.from(Glyph.CANCEL, Color.BLACK, 12.0)
        }
    }

    override fun onDock() {
        super.onDock()
        changeProgressInactive()
    }

    fun changeProgressActive() {
        viewProgress.show()
        viewButtonRefresh.hide()
        viewButtonStop.show()
    }

    fun changeProgressInactive() {
        viewProgress.hide()
        viewButtonRefresh.show()
        viewButtonStop.hide()
    }

}
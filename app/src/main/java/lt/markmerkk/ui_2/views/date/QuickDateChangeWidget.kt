package lt.markmerkk.ui_2.views.date

import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.ui_2.views.jfxButton
import org.slf4j.LoggerFactory
import tornadofx.View
import tornadofx.hbox
import tornadofx.style

class QuickDateChangeWidget(
        private val graphics: Graphics<SVGGlyph>,
        private val presenter: DateChangeContract.Presenter
) : View(), DateChangeContract.View {

    private lateinit var viewArrowLeft: JFXButton
    private lateinit var viewArrowRight: JFXButton
    private lateinit var viewButtonDate: JFXButton

    override val root: Parent = hbox {
        viewArrowLeft = jfxButton {
            graphic = graphics.from(Glyph.ARROW_LEFT, Color.BLACK, 6.0, 8.0)
            setOnAction { presenter.onClickPrev() }
        }
        viewButtonDate = jfxButton("Choose date") {
            setOnAction { presenter.onClickDate() }
        }
        viewArrowRight = jfxButton {
            graphic = graphics.from(Glyph.ARROW_RIGHT, Color.BLACK, 6.0, 8.0)
            setOnAction { presenter.onClickNext() }
        }
    }

    override fun onAttach() {
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
    }

    override fun render(title: String) {
        viewButtonDate.text = title
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.MAIN)
    }

}
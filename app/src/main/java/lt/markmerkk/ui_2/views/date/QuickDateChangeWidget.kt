package lt.markmerkk.ui_2.views.date

import com.jfoenix.controls.JFXButton
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import lt.markmerkk.*
import lt.markmerkk.ui_2.views.jfxButton
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class QuickDateChangeWidget: Fragment(), DateChangeContract.View {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var logStorage: LogStorage

    init {
        Main.component().inject(this)
    }

    private lateinit var presenter: DateChangeContract.Presenter

    private lateinit var viewArrowLeft: JFXButton
    private lateinit var viewArrowRight: JFXButton
    private lateinit var viewButtonDate: JFXButton

    override val root: Parent = hbox {
        style {
            backgroundColor.add(Paint.valueOf(MaterialColors.LIGHTEST))
            backgroundRadius.add(box(20.px))
        }
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

    override fun onDock() {
        super.onDock()
        presenter = QuickDateChangeWidgetPresenterDefault(logStorage)
        presenter.onAttach(this)
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
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
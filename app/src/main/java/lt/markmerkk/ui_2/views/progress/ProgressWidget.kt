package lt.markmerkk.ui_2.views.progress

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXProgressBar
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Parent
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSpinner
import tornadofx.*

class ProgressWidget(
        private val presenter: ProgressContract.Presenter,
        private val graphics: Graphics<SVGGlyph>
): View(), ProgressContract.View {

    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewStart: JFXButton

    override val root: Parent = stackpane {
        viewStart = jfxButton {
            graphic = graphics.from(Glyph.REFRESH2, Color.BLACK, 12.0)
            setOnAction { presenter.onClickSync() }
        }
        viewProgress = jfxSpinner {
            minWidth = 18.0
            minHeight = 18.0
            maxWidth = 18.0
            maxHeight = 18.0
        }
        hideProgress()
    }

    override fun onAttach() {
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
    }

    override fun showProgress() {
        viewStart.isVisible = false
        viewStart.isManaged = false
        viewProgress.isVisible = true
        viewProgress.isManaged = true
    }

    override fun hideProgress() {
        viewStart.isVisible = true
        viewStart.isManaged = true
        viewProgress.isVisible = false
        viewProgress.isManaged = false
    }
}
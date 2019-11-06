package lt.markmerkk.ui_2.views.progress

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXProgressBar
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.MaterialColors
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSpinner
import tornadofx.*

class ProgressWidget(
        private val presenter: ProgressContract.Presenter,
        private val graphics: Graphics<SVGGlyph>
): Fragment(), ProgressContract.View {

    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewStart: JFXButton
    private lateinit var viewLabel: Label

    override val root: Parent = borderpane {
        left {
            vbox(alignment = Pos.CENTER) {
                viewLabel = label("") {
                    style {
                        padding = box(2.0.px)
                    }
                }
            }
        }
        right {
            stackpane {
                viewStart = jfxButton {
                    graphic = graphics.from(Glyph.REFRESH2, Color.BLACK, 12.0)
                    setOnAction { presenter.onClickSync() }
                }
                viewProgress = jfxSpinner {
                    style {
                        padding = box(2.0.px)
                    }
                    minWidth = 24.0
                    minHeight = 24.0
                    prefWidth = 24.0
                    prefHeight = 24.0
                    maxWidth = 24.0
                    maxHeight = 24.0
                }
                hideProgress()
                style {
                    backgroundColor.add(Paint.valueOf(MaterialColors.LIGHTEST))
                    backgroundRadius.add(box(20.px))
                }
            }
        }
    }

    override fun onAttach() {
        presenter.onAttach(this)
    }

    override fun onDetach() {
        presenter.onDetach()
    }

    override fun showProgress() {
        viewStart.hide()
        viewProgress.show()
    }

    override fun hideProgress() {
        viewStart.show()
        viewProgress.hide()
    }

    override fun changeLabel(syncData: String) {
        viewLabel.text = syncData
    }

    fun checkSyncDuration() {
        presenter.checkSyncDuration()
    }

}
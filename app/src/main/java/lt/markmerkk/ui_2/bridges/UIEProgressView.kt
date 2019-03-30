package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.svg.SVGGlyph
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.ui.UIElement

/**
 * Represents progress when logs are being synchronized
 */
class UIEProgressView(
        private val jfxContainerContentRefresh: StackPane,
        private val jfxButtonProgressRefresh: JFXButton,
        private val jfxButtonProgressStop: JFXButton,
        private val jfxSpinnerProgress: JFXSpinner,
        private val graphics: Graphics<SVGGlyph>,
        private val refreshListener: RefreshListener
) : UIElement<StackPane>, IRemoteLoadListener {

    init {
        jfxSpinnerProgress.prefWidth = 12.0
        jfxSpinnerProgress.prefHeight = 12.0
        jfxButtonProgressRefresh.setOnAction { refreshListener.onClickRefresh() }
        jfxButtonProgressStop.setOnAction { refreshListener.onClickStop() }
        jfxButtonProgressRefresh.graphic = graphics.from(Glyph.REFRESH2, Color.BLACK, 12.0)
        jfxButtonProgressStop.graphic = graphics.from(Glyph.CANCEL, Color.BLACK, 12.0)
        hide()
    }

    override fun raw() = jfxContainerContentRefresh

    override fun show() {
        jfxSpinnerProgress.isVisible = true
        jfxButtonProgressRefresh.isVisible = false
        jfxButtonProgressRefresh.isManaged = false
        jfxButtonProgressStop.isVisible = true
        jfxButtonProgressStop.isManaged = true
    }

    override fun hide() {
        jfxSpinnerProgress.isVisible = false
        jfxButtonProgressRefresh.isVisible = true
        jfxButtonProgressRefresh.isManaged = true
        jfxButtonProgressStop.isVisible = false
        jfxButtonProgressStop.isManaged = false
    }

    override fun reset() {
        hide()
    }

    override fun onLoadChange(loading: Boolean) {
        if (loading) {
            show()
        } else {
            hide()
        }
    }

    override fun onError(error: String) {
        hide()
    }

    interface RefreshListener {
        fun onClickRefresh()
        fun onClickStop()
    }

}
package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.svg.SVGGlyph
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Graphics
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.ui.UIElement

/**
 * Represents progress when logs are being synchronized
 */
class UIEProgressView(
        private val jfxContainerContentRefresh: StackPane,
        private val jfxButtonRefresh: JFXButton,
        private val jfxSpinner: JFXSpinner,
        private val graphics: Graphics<SVGGlyph>,
        private val syncInteractor: SyncInteractor
) : UIElement<StackPane>, IRemoteLoadListener {

    private val glyphCancel = graphics.glyph("cancel", Color.BLACK, 12.0)
    private val glyphRefresh = graphics.glyph("refresh2", Color.BLACK, 12.0)

    init {
        jfxButtonRefresh.setOnAction {
            if (!syncInteractor.isLoading()) {
                syncInteractor.syncLogs()
            } else {
                syncInteractor.stop()
            }
        }
        hide()
    }

    override fun raw() = jfxContainerContentRefresh

    override fun show() {
        jfxSpinner.isVisible = true
        jfxButtonRefresh.graphic = glyphCancel
    }

    override fun hide() {
        jfxSpinner.isVisible = false
        jfxButtonRefresh.graphic = glyphRefresh
    }

    override fun reset() {}

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

}
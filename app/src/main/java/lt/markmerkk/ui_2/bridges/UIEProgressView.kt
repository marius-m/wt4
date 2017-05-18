package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXProgressBar
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Pane
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.ui.UIElement

/**
 * Represents settings button, its graphics, animations
 */
class UIEProgressView(
        private val rootPane: Pane,
        private val progressBar: JFXProgressBar
) : UIElement<JFXProgressBar>, IRemoteLoadListener {

    init {
        rootPane.widthProperty().addListener(object : ChangeListener<Number> {
            override fun changed(
                    observable: ObservableValue<out Number>?,
                    oldValue: Number?,
                    newValue: Number
            ) {
                progressBar.prefWidth = newValue.toDouble()
            }
        })
        hide()
    }

    override fun raw(): JFXProgressBar = progressBar

    override fun show() {
        progressBar.isManaged = true
        progressBar.isVisible = true
    }

    override fun hide() {
        progressBar.isManaged = false
        progressBar.isVisible = false
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

}
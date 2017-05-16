package lt.markmerkk.ui_2.bridges

import com.airhacks.afterburner.views.FXMLView
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import lt.markmerkk.ui.UIElement

/**
 * Represents clock jfxCommitButton, its graphics, animations
 */
class UIECenterView(
        private val jfxMain: BorderPane
) : UIElement<VBox> {

    private var container: VBox = VBox()

    init {
    }

    override fun raw(): VBox = container

    override fun show() { }

    override fun hide() { }

    override fun reset() { }

    fun populate(fxmlView: FXMLView) {
        container = VBox(fxmlView.view)
        (fxmlView.view as Region).padding = Insets(0.0, 0.0, 0.0, 0.0)
        VBox.setVgrow(fxmlView.view, Priority.ALWAYS)
        VBox.setMargin(fxmlView.view, Insets(6.0, 6.0, 6.0, 6.0))
        jfxMain.center = container
    }

}
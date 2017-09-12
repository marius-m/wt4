package lt.markmerkk.ui_2.bridges

import com.airhacks.afterburner.views.FXMLView
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.*
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.LogStatusView

/**
 * Represents clock jfxCommitButton, its graphics, animations
 */
class UIECenterView(
        private val jfxMain: BorderPane
) : UIElement<StackPane> {

    private var container: StackPane = StackPane()
//    private var statusBar: Label = Label("Test")

    init { }

    override fun raw(): StackPane = container

    override fun show() { }

    override fun hide() { }

    override fun reset() { }

    fun populate(fxmlView: FXMLView) {
        container.children.clear()

        // Adding main container
        container.children.add(fxmlView.view)
        (fxmlView.view as Region).padding = Insets(0.0, 0.0, 0.0, 0.0)
        VBox.setVgrow(fxmlView.view, Priority.ALWAYS)
        VBox.setMargin(fxmlView.view, Insets(0.0, 0.0, 0.0, 0.0))
        jfxMain.center = container
    }

}
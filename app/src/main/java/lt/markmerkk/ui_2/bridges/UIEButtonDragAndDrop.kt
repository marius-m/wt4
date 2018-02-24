package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Strings
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement


/**
 * Represents clock jfxCommitButton, its graphics, animations
 */
class UIEButtonDragAndDrop(
        private val node: ExternalSourceNode<StackPane>,
        private val strings: Strings,
        private val onClick: () -> Unit
) : UIElement<JFXButton> {

    private val button: JFXButton = JFXButton(strings.getString("calendar_button_edit_title"))

    init {
        node.rootNode().children.add(button)
        StackPane.setAlignment(button, Pos.TOP_RIGHT)
        StackPane.setMargin(button, Insets(10.0))
        val backgroundFill = BackgroundFill(
                Color.web("#E91E63"),
                CornerRadii(10.0),
                Insets(0.0, 0.0, 0.0, 0.0)
        )
        button.background = Background(backgroundFill)
        button.textFill = Color.WHITE
        button.setOnAction { onClick.invoke() }
    }

    override fun raw(): JFXButton = button

    override fun show() {
        button.isVisible = true
        button.isManaged = true
    }

    override fun hide() {
        button.isVisible = false
        button.isManaged = false
    }

    override fun reset() {}

}
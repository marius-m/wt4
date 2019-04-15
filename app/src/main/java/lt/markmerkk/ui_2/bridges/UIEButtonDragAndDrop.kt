package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXButton
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.Strings
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui_2.views.QuickEditView


class UIEButtonDragAndDrop(
        private val node: ExternalSourceNode<StackPane>,
        private val strings: Strings,
        private val onClick: () -> Unit
) : UIElement<Node> {

    private val viewQuickEdit = QuickEditView()

    init {
        node.rootNode().children.add(viewQuickEdit.root)
        StackPane.setAlignment(viewQuickEdit.root, Pos.TOP_RIGHT)
        StackPane.setMargin(viewQuickEdit.root, Insets(10.0, 20.0, 0.0, 0.0))
    }

    override fun raw(): Node = viewQuickEdit.root

    override fun show() {
        viewQuickEdit.root.isVisible = true
        viewQuickEdit.root.isManaged = true
    }

    override fun hide() {
        viewQuickEdit.root.isVisible = false
        viewQuickEdit.root.isManaged = false
    }

    override fun reset() {}

}
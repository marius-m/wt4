package lt.markmerkk.ui_2.views

import com.jfoenix.controls.*
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import tornadofx.addChildIfPossible
import tornadofx.attachTo
import tornadofx.bind
import tornadofx.observable

/**
 * Add the given node to the pane, invoke the node operation and return the node. The `opcr` name
 * is an acronym for "op connect & return".
 */
inline fun <T : Node> opcr(parent: EventTarget, node: T, op: T.() -> Unit = {}) = node.apply {
    parent.addChildIfPossible(this)
    op(this)
}

/**
 * Attaches the node to the pane and invokes the node operation.
 */
inline fun <T : Node> T.attachTo(parent: EventTarget, op: T.() -> Unit = {}): T = opcr(parent, this, op)

/**
 * Attaches the node to the pane and invokes the node operation.
 * Because the framework sometimes needs to setup the node, another lambda can be provided
 */
internal inline fun <T : Node> T.attachTo(
        parent: EventTarget,
        after: T.() -> Unit,
        before: (T) -> Unit
) = this.also(before).attachTo(parent, after)

// Buttons
fun EventTarget.jfxButton(
        text: String = "",
        graphic: Node? = null,
        op: Button.() -> Unit = {}
) = JFXButton(text).attachTo(this, op) {
    if (graphic != null) it.graphic = graphic
}

// Spinner
fun EventTarget.jfxSpinner(op: JFXSpinner.() -> Unit = {}) = JFXSpinner().attachTo(this, op)

// Slider
fun EventTarget.jfxSlider(op: JFXSlider.() -> Unit = {}) = JFXSlider().attachTo(this, op)

// Text
fun EventTarget.jfxTextArea(op: JFXTextArea.() -> Unit = {}) = JFXTextArea().attachTo(this, op)
fun EventTarget.jfxTextField(op: JFXTextField.() -> Unit = {}) = JFXTextField().attachTo(this, op)

// Combo
fun <T> EventTarget.jfxCombobox(
        property: Property<T>? = null,
        values: List<T>? = null,
        op: JFXComboBox<T>.() -> Unit = {}) = JFXComboBox<T>().attachTo(this, op) {
    if (values != null) it.items = values as? ObservableList<T> ?: values.observable()
    if (property != null) it.bind(property)
    it.selectionModel.select(0)
}

// Dialog
fun EventTarget.jfxDialog(op: JFXDialog.() -> Unit = {}) = JFXDialog().attachTo(this, op)
fun EventTarget.jfxDialogLayout(op: JFXDialogLayout.() -> Unit = {}) = JFXDialogLayout().attachTo(this, op)

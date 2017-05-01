package lt.markmerkk.ui_2.bridges

import javafx.scene.Node

/**
 * Encapsulates simple show/hide logic for a separate view container
 */
interface UIElement<out Node> {
    /**
     * Returns a raw node element
     */
    fun raw(): Node

    /**
     * Should handle "show" action
     */
    fun show()

    /**
     * Should handle "hide" action
     */
    fun hide()
}
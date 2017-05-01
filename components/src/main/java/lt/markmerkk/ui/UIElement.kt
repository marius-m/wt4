package lt.markmerkk.ui

/**
 * Encapsulates simple show/hide logic for a separate view container
 */
interface UIElement<out Node> {
    /**
     * Returns a raw node element
     */
    fun raw(): Node

    /**
     * Sets component to "shown" state
     */
    fun show()

    /**
     * Sets component to "hidden" state
     */
    fun hide()

    /**
     * Resets component to initial state
     */
    fun reset()
}
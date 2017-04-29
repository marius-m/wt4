package lt.markmerkk.ui_2.bridges

/**
 * Encapsulates simple show/hide logic for a separate view container
 */
interface UIElement {
    /**
     * Should handle "show" action
     */
    fun show()

    /**
     * Should handle "hide" action
     */
    fun hide()
}
package lt.markmerkk.ui

/**
 * UIElement that can update text
 */
interface UIElementText<out T> : UIElement<T> {
    /**
     * Update its text
     */
    fun updateText(text: String)
}
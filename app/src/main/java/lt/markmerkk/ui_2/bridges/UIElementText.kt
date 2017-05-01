package lt.markmerkk.ui_2.bridges

/**
 * UIElement that can update text
 */
interface UIElementText<out T> : UIElement<T> {
    /**
     * Update its text
     */
    fun updateText(text: String)
}
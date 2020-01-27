package lt.markmerkk.utils

/**
 * Provides all the utility functions to help display the UIElements
 */
object UIEUtils {
    /**
     * Defines font size based on text length
     * Structured to fix text for action buttons
     */
    fun fontSizeBasedOnLength(text: String): Double {
        return when {
            text.length <= 2 -> 14.0
            text.length <= 3 -> 12.0
            text.length <= 5 -> 10.0
            else -> 8.0
        }
    }
}
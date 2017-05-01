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
        if (text.length <= 2) {
            return 14.0
        } else if (text.length <= 3) {
            return 12.0
        } else if (text.length <= 5) {
            return 10.0
        }
        return 8.0
    }
}
package lt.markmerkk

import javafx.scene.paint.Color

/**
 * Provides graphics
 */
interface Graphics<T> {
    /**
     * Provides graphic by the string key. Else will provide alert graphic.
     */
    fun glyph(
            key: String,
            color: Color,
            size: Double
    ): T

    /**
     * Provides graphic by the string key. Else will provide alert graphic.
     */
    fun glyph(
            key: String,
            color: Color,
            width: Double,
            height: Double
    ): T
}

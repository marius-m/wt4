package lt.markmerkk

import javafx.scene.paint.Color

/**
 * Provides graphics
 */
interface Graphics<T> {

    fun from(
        glyph: Glyph,
        color: Color,
        size: Double
    ): T

    fun from(
        glyph: Glyph,
        color: Color,
        width: Double,
        height: Double
    ): T

    fun from(
        glyph: Glyph,
        color: Color,
        width: Double,
        height: Double,
        rotate: Double
    ): T

}

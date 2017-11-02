package lt.markmerkk

import com.jfoenix.svg.SVGGlyph
import javafx.scene.paint.Color
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

/**
 * @author mariusmerkevicius
 * @since 2017-09-17
 */
class GraphicsImpl : Graphics<SVGGlyph> {

    lateinit var glyphs: Map<String, String>

    override fun glyph(
            key: String,
            color: Color,
            width: Double,
            height: Double
    ): SVGGlyph {
        if (!glyphs.containsKey(key)) {
            return graphAlert(color, width, height)
        }
        val svgGlyph = SVGGlyph(
                -1,
                key,
                glyphs[key],
                color
        )
        svgGlyph.setSize(width, height)
        return svgGlyph
    }

    override fun glyph(
            key: String,
            color: Color,
            size: Double
    ): SVGGlyph {
        return glyph(key, color, size, size)
    }

    init {
        try {
            val glyphsAsProperties = Properties()
            val resourceAsStream = javaClass.getResourceAsStream("/graphics.properties")
            glyphsAsProperties.load(resourceAsStream)
            val glyphs = mutableMapOf<String, String>()
            glyphsAsProperties.forEach { key, value -> glyphs.put(key as String, value as String) }
            this.glyphs = glyphs.toMap()
        } catch (e: IOException) {
            logger.error("[ERROR] No glyphs were initialized!", e)
        }
    }

    private fun graphAlert(
            color: Color,
            width: Double,
            height: Double
    ): SVGGlyph {
        val svgGlyph = SVGGlyph(
                -1,
                "alert",
                "M13,13H11V7H13M13,17H11V15H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z",
                color
        )
        svgGlyph.setSize(width, height)
        return svgGlyph
    }

    companion object {
        val logger = LoggerFactory.getLogger(Graphics::class.java)!!
    }

}
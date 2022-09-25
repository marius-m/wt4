package lt.markmerkk.widgets.help.html.styles

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class JfxConverterH1 : HtmlToJfxMapper.JfxConverter {
    override fun canConvert(htmlNode: Node): Boolean = htmlNode is Element
        && htmlNode.tagName().equals("h1", ignoreCase = true)

    override fun convertToJfxProps(htmlNode: Node): Map<String, String> {
        return mapOf(
            "-fx-font-family" to "sans-serif",
            "-fx-font-size" to "18",
            "-fx-font-weight" to "700",
        )
    }
}
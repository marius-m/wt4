package lt.markmerkk.widgets.help.html.styles

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class JfxConverterParagraph : HtmlToJfxMapper.JfxConverter {
    override fun canConvert(htmlNode: Node): Boolean = htmlNode is Element
        && htmlNode.tagName().equals("p", ignoreCase = true)

    override fun convertToJfxProps(htmlNode: Node): Map<String, String> {
        return mapOf(
            "-fx-font-family" to "sans-serif",
            "-fx-font-size" to "12",
        )
    }
}
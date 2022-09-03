package lt.markmerkk.widgets.help.html.styles

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class JfxConverterBold : HtmlToJfxMapper.JfxConverter {
    override fun canConvert(htmlNode: Node): Boolean = htmlNode is Element
        && htmlNode.tagName().equals("b", ignoreCase = true)

    override fun convertToJfxProps(htmlNode: Node): Map<String, String> {
        return mapOf(
            "-fx-font-weight" to "700",
        )
    }
}
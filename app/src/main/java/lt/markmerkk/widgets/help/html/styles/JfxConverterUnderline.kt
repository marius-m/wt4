package lt.markmerkk.widgets.help.html.styles

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class JfxConverterUnderline : HtmlToJfxMapper.JfxConverter {
    override fun canConvert(htmlNode: Node): Boolean = htmlNode is Element
        && htmlNode.tagName().equals("u", ignoreCase = true)

    override fun convertToJfxProps(htmlNode: Node): Map<String, String> {
        return mapOf(
            "-fx-underline" to  "true",
        )
    }
}
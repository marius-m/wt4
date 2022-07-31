package lt.markmerkk.widgets.help.html.styles

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class JfxConverterFont : HtmlToJfxMapper.JfxConverter {
    override fun canConvert(htmlNode: Node): Boolean {
        return htmlNode is Element
            && htmlNode.tag().name.equals("font")
    }

    override fun convertToJfxProps(htmlNode: Node): Map<String, String> {
        val attrs = htmlNode.attributes()
        val mutableProps = mutableMapOf<String, String>()
        if (attrs.hasKey(ATTR_KEY_COLOR)) {
            mutableProps["-fx-fill"] = attrs.getIgnoreCase(ATTR_KEY_COLOR)
        }
        if (attrs.hasKey(ATTR_KEY_SIZE)) {
            mutableProps["-fx-font-size"] = attrs.getIgnoreCase(ATTR_KEY_SIZE)
        }
        if (attrs.hasKey(ATTR_KEY_STRIKETHROUGH)) {
            mutableProps["-fx-strikethrough"] = if (attrs.getIgnoreCase(ATTR_KEY_STRIKETHROUGH).equals("true")) {
                "true"
            } else {
                "false"
            }
        }
        return mutableProps.toMap()
    }

    companion object {
        const val ATTR_KEY_COLOR = "color"
        const val ATTR_KEY_SIZE = "size"
        const val ATTR_KEY_STRIKETHROUGH = "strikethrough"
    }
}
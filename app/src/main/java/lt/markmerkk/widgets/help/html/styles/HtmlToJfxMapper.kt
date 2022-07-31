package lt.markmerkk.widgets.help.html.styles

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class HtmlToJfxMapper(
    private val converters: Set<JfxConverter>,
) {

    /**
     * Only a handful of nodes have style representations ([JfxConverter])
     * This functions finds those nodes and applies a style if available
     *
     * Useful when want to apply a style to a [Node] based on what parents it has
     *
     * Also works with nested html tags
     * @return list of style map or empty
     */
    fun jfxPropsFromNodes(
        nodes: List<Node>,
    ): Map<String, String> {
        val styleMap: Map<String, String> = nodes
            .filterIsInstance<Element>()
            .reversed()
            .fold(mutableMapOf<String, String>()) { accumulator, node ->
                accumulator.putAll(jfxPropsFromNode(node))
                accumulator
            }
        return styleMap
    }

    fun jfxPropsFromNode(htmlNode: Node): Map<String, String> {
        val handlableConverter = converters
            .firstOrNull { it.canConvert(htmlNode) }
        if (handlableConverter != null) {
            return handlableConverter.convertToJfxProps(htmlNode)
        }
        return emptyMap()
    }

    interface JfxConverter {
        fun canConvert(htmlNode: Node): Boolean
        fun convertToJfxProps(htmlNode: Node): Map<String, String>
    }

    companion object {
        fun asDefault(): HtmlToJfxMapper {
            return HtmlToJfxMapper(
                converters = setOf(
                    JfxConverterH1(),
                    JfxConverterParagraph(),
                    JfxConverterBold(),
                    JfxConverterItalic(),
                    JfxConverterFont(),
                    JfxConverterUnderline(),
                )
            )
        }
    }
}
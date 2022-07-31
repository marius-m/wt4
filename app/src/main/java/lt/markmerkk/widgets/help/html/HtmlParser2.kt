package lt.markmerkk.widgets.help.html

import lt.markmerkk.widgets.help.html.utils.TraverserFindTextNode
import lt.markmerkk.widgets.help.html.utils.TraverserParentsCrawler
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

class HtmlParser2 {

    private var styledText = StyledText()

    fun parse(inputRaw: String): StyledText {
        val doc: Document = Jsoup.parse(inputRaw)
        val textNodes = TraverserFindTextNode.find(doc = doc)
        this.styledText = convertTextNodesToStyle(textNodes = textNodes)
        return styledText
    }

    private fun convertTextNodesToStyle(
        textNodes: List<TextNode>,
    ): StyledText {
        val styledText = StyledText()
        textNodes.forEach { textNode ->
            val textNodeParents = TraverserParentsCrawler.crawl(textNode)
            val styles: Map<String, String> = styleNodesToStyleName(nodes = textNodeParents)
            if (styles.isEmpty()) {
                styledText.appendTextBasic(text = textNode.wholeText)
            } else {
                styledText.appendTextWithStyles(
                    text= textNode.wholeText,
                    styles = styles,
                )
            }
        }
        return styledText
    }

    /**
     * Only a handful of nodes have style representations.
     * This functions finds those nodes and applies a style if available
     * @return list of styles or empty
     */
    private fun styleNodesToStyleName(
        nodes: List<Node>,
    ): Map<String, String> {
        val styleMap: Map<String, String> = nodes
            .filterIsInstance<Element>()
            .reversed()
            .fold(mutableMapOf<String, String>()) { accumulator, node ->
                val styleMap: Map<String, String> = when {
                    node.tagName() == "h1" -> {
                        mapOf(
                            "-fx-font-family" to "sans-serif",
                            "-fx-font-size" to "8",
                            "-fx-fill" to "red",
                        )
                    }
                    node.tagName() == "b" -> {
                        mapOf(
                            "-fx-font-family" to  "sans-serif",
                            "-fx-font-weight" to "700",
                            "-fx-font-size" to "14",
                            "-fx-fill" to "blue",
                        )
                    }
                    else -> emptyMap()
                }
                accumulator.putAll(styleMap)
                accumulator
            }
        return styleMap
    }

}

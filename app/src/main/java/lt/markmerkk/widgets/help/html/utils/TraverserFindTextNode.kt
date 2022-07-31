package lt.markmerkk.widgets.help.html.utils

import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Responsible for finding [TextNode]
 */
object TraverserFindTextNode {

    fun find(doc: Document): List<TextNode> {
        val textNodes = mutableListOf<TextNode>()
        traverseAndFindAllTextNodes(mutableTextNodes = textNodes, nodes = doc.childNodesCopy())
        return textNodes.toList()
    }

    private fun traverseAndFindAllTextNodes(
        mutableTextNodes: MutableList<TextNode> = mutableListOf(),
        nodes: List<Node>,
    ) {
        for (node in nodes) {
            if (node is TextNode) {
                mutableTextNodes.add(node)
            } else {
                if (node.childNodeSize() > 0) {
                    traverseAndFindAllTextNodes(
                        mutableTextNodes = mutableTextNodes,
                        nodes = node.childNodes(),
                    )
                }
            }
        }
    }
}
package lt.markmerkk.widgets.help.html.utils

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

/**
 * Crawls through hierarchy and finds all parents
 */
object TraverserParentsCrawler {

    fun crawl(targetNode: Node): List<Node> {
        val nodeParents = mutableListOf<Node>()
        traverseAndFindAllParents(
            mutableParentNodes = nodeParents,
            node = targetNode,
        )
        return nodeParents.toList()
    }

    private fun traverseAndFindAllParents(
        mutableParentNodes: MutableList<Node> = mutableListOf(),
        node: Node?,
    ) {
        if (node != null && node.hasParent()) {
            val parentNode = node.parentNode()!!
            mutableParentNodes.add(parentNode)
            traverseAndFindAllParents(
                mutableParentNodes = mutableParentNodes,
                node = parentNode,
            )
        }
    }
}
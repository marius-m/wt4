package lt.markmerkk.widgets.help.html

import lt.markmerkk.widgets.help.html.styles.HtmlToJfxMapper
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
        val htmlToJfxMapper = HtmlToJfxMapper.asDefault()
        val styledText = StyledText()
        textNodes.forEach { textNode ->
            val textNodeParents = TraverserParentsCrawler.crawl(textNode)
            val styles: Map<String, String> = htmlToJfxMapper.jfxPropsFromNodes(
                nodes = textNodeParents,
            )
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


}

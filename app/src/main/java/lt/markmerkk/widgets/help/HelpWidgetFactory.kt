package lt.markmerkk.widgets.help

import javafx.scene.Node
import lt.markmerkk.widgets.edit.timepick.PopOverConfigHelp
import lt.markmerkk.widgets.help.html.HtmlParser2
import lt.markmerkk.widgets.help.html.StyledText
import org.controlsfx.control.PopOver
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea
import org.fxmisc.richtext.StyleClassedTextArea
import tornadofx.SVGIcon
import tornadofx.add
import tornadofx.box
import tornadofx.hbox
import tornadofx.px
import tornadofx.style

class HelpWidgetFactory(
    private val imgResLoader: ImgResourceLoader,
    private val helpResLoader: HelpResourceLoader
) {

    fun createHelpIconWith(
        anchorNode: Node,
        helpRes: ResourceHelp,
        popOverConfig: PopOverConfigHelp = PopOverConfigHelp(title = helpRes.title),
    ): SVGIcon {
        return SVGIcon(imgResLoader.imageResRaw(ResourceSvg.HELP), size = 14).apply {
            setOnMouseClicked {
                PopOver(
                    hbox {
                        style {
                            padding = box(all = 10.px)
                        }
                        val area = InlineCssTextArea().apply {
                            appendText(helpResLoader.helpResRaw(helpRes))
                            isWrapText = true
                            isEditable = false
                            prefWidth = 300.0
                            prefHeight = 200.0
                            applyStyle(this)
                        }
                        val vsPane = VirtualizedScrollPane(area)
                        add(vsPane)
                    }
                ).apply {
                    popOverConfig.applyValues(this)
                }.show(anchorNode)
            }
        }
    }

    private fun applyStyle(
        // area: StyleClassedTextArea,
        area: InlineCssTextArea,
    ) {
        area.style = "-fx-font: sans-serif;"
        val htmlParser = HtmlParser2()
        val originalText = area.text
        val styledText = htmlParser.parse(originalText)
        area.deleteText(0, originalText.length - 1)
        area.insertText(0, styledText.text())
        styledText.elements().forEach { styleElement ->
            when (styleElement) {
                is StyledText.ElementNoStyle -> {}
                is StyledText.ElementStyleBasic -> {
                    area.setStyle(
                        styleElement.range.start,
                        styleElement.range.endInclusive,
                        styleElement.stylesAsString(),
                    )
                }
            }
        }
        // area.setStyleClass(0,  10, "blue")
        // area.setStyleClass(10,  20, "bold")
    }

}
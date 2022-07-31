package lt.markmerkk.widgets.help

import javafx.scene.Node
import lt.markmerkk.widgets.edit.timepick.PopOverConfigHelp
import lt.markmerkk.widgets.help.html.HtmlParser
import lt.markmerkk.widgets.help.html.StyledText
import org.controlsfx.control.PopOver
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea
import org.fxmisc.richtext.model.SimpleEditableStyledDocument
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
        anchorNode: Node? = null,
        helpRes: ResourceHelp,
        popOverConfig: PopOverConfigHelp = PopOverConfigHelp(title = helpRes.title),
    ): SVGIcon {
        return SVGIcon(imgResLoader.imageResRaw(ResourceSvg.HELP), size = 14).apply {
            val popAnchor = anchorNode ?: this
            setOnMouseClicked {
                PopOver(
                    hbox {
                        style {
                            padding = box(all = 10.px)
                        }
                        val initialStyle = "-fx-font-face: sans-serif; -fx-font-size: 20; "
                        val area = InlineCssTextArea(SimpleEditableStyledDocument(initialStyle, initialStyle))
                            .apply {
                                isWrapText = true
                                isEditable = false
                                prefWidth = 300.0
                                prefHeight = 200.0
                                applyStyleWithText(
                                    area = this,
                                    text = helpResLoader.helpResRaw(helpRes),
                                )
                            }
                        val vsPane = VirtualizedScrollPane(area)
                        add(vsPane)
                    }
                ).apply {
                    popOverConfig.applyValues(this)
                }.show(popAnchor)
            }
        }
    }

    private fun applyStyleWithText(
        area: InlineCssTextArea,
        text: String,
    ) {
        val styledText = HtmlParser().parse(text)
        area.appendText(styledText.text())
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
    }

}
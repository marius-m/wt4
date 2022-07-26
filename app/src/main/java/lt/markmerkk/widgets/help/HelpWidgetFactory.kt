package lt.markmerkk.widgets.help

import com.github.rjeschke.txtmark.Processor
import javafx.scene.Node
import lt.markmerkk.widgets.edit.timepick.PopOverConfigHelp
import org.controlsfx.control.PopOver
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
                        // markdown(helpResLoader.helpResRaw(helpRes)) {
                        //     minWidth = 320.0
                        // }
                        // textarea {
                        // }
                        val result: String = Processor
                            .process(helpResLoader.helpResRaw(helpRes))
                        val area = StyleClassedTextArea()
                        area.isWrapText = true
                        area.appendText(result)
                        area.prefWidth = 300.0
                        area.prefHeight = 200.0
                        add(area)
                    }
                ).apply {
                    popOverConfig.applyValues(this)
                }.show(anchorNode)
            }
        }
    }

}
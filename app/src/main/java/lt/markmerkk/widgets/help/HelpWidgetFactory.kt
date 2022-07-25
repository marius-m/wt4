package lt.markmerkk.widgets.help

import javafx.scene.Node
import lt.markmerkk.widgets.edit.timepick.PopOverConfig
import lt.markmerkk.widgets.edit.timepick.PopOverConfigHelp
import org.controlsfx.control.PopOver
import tornadofx.*


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
                        textarea {
                            text = helpResLoader.helpResRaw(helpRes)
                        }
                    }
                ).apply {
                    popOverConfig.applyValues(this)
                }.show(anchorNode)
            }
        }
    }

}
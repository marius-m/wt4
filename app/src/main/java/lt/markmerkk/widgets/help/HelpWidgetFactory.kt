package lt.markmerkk.widgets.help

import javafx.scene.Node
import org.controlsfx.control.PopOver
import tornadofx.*


class HelpWidgetFactory(
    private val imgResLoader: ImgResourceLoader,
    private val helpResLoader: HelpResourceLoader
) {

    fun createHelpIconWith(
        anchorNode: Node,
        helpRes: ResourceHelp
    ): SVGIcon {
        return SVGIcon(imgResLoader.imageResRaw(ResourceSvg.HELP), size = 14).apply {
            setOnMouseClicked {
                PopOver(
                    hbox {
                        style {
                            padding = box(all = 10.px)
                        }
                        markdown(helpResLoader.helpResRaw(helpRes)) {
                            minWidth = 320.0
                        }
                    }
                ).apply {
                    title = "test"
                    arrowLocation = PopOver.ArrowLocation.RIGHT_CENTER
                }.show(anchorNode)
            }
        }
    }

}
package lt.markmerkk.widgets

import com.jfoenix.controls.JFXPopup
import com.jfoenix.svg.SVGGlyph
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import lt.markmerkk.Styles
import lt.markmerkk.ui_2.BaseView
import lt.markmerkk.ui_2.views.jfxButton
import tornadofx.*

interface PopUpDisplay {

    fun show()

    /**
     * Generates and shows popup with predefined actions
     */
    fun createPopUpDisplay(
            actions: List<PopUpAction>,
            attachTo: Node
    ) {
        val viewPopUp = JFXPopup()
        val viewContainer = object : BaseView() {
            override val root: Parent = vbox(spacing = 4) {
                style {
                    padding = box(4.px)
                }
                actions.forEach { popUpAction ->
                    jfxButton(popUpAction.title) {
                        addClass(Styles.popUpLabel)
                        graphic = popUpAction.graphic
                        action {
                            popUpAction.action.invoke()
                            viewPopUp.hide()
                        }
                    }
                }
            }
        }
        viewPopUp.popupContent = viewContainer.root as VBox
        viewPopUp.show(attachTo, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT)
    }
}

class PopUpAction(
        val title: String,
        val graphic: SVGGlyph,
        val action: () -> Unit
)

package lt.markmerkk

import com.jfoenix.controls.JFXButton
import javafx.scene.control.OverrunStyle
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class Styles: Stylesheet() {

    private val fontEmoji = Styles::class.java.getResourceAsStream("/fonts/OpenSansEmoji.ttf")
            .use { Font.loadFont(it, 24.0) }

    companion object {
        val jfxButtonType by cssproperty<JFXButton.ButtonType>("-jfx-button-type") { it.name }

        val dialogHeader by cssclass()
        val sidePanelHeader by cssclass()
        val sidePanelContainer by cssclass()
        val dialogContainer by cssclass()
        val dialogContainerActionsButtons by cssclass()
        val dialogButtonAction by cssclass()
        val buttonMenu by cssclass()
        val buttonMenuMini by cssclass()
        val inputTextField by cssclass()
        val labelMini by cssclass()
        val labelRegular by cssclass()
        val popUpLabel by cssclass()
        val emojiText by cssclass()
        val textMini by cssclass()

        val cLightest = c("#E8EAF6")
        val cLight = c("#7986CB")
        val cPrimary = c("#5C6BC0")
        val cDark = c("#303F9F")
        val cDarkest = c("#1A237E")

        val cActivateLightest = c("#FFD180")
        val cActivateLight = c("#FFAB40")
        val cActivatePrimary = c("#FF9800")
        val cActivateDark = c("#FF9100")
        val cActivateDarkest = c("#FF6D00")

        val cActiveRed = c("#E91E63")
        val cActiveOrange = Color.ORANGE
        val cBackgroundPrimary = c("#5C6BC0")
    }

    init {
        sidePanelContainer {
            padding = box(
                    top = 10.px,
                    left = 20.px,
                    right = 20.px,
                    bottom = 10.px
            )
        }
        dialogContainer {
            minWidth = 400.px
            padding = box(
                    top = 10.px,
                    left = 20.px,
                    right = 20.px,
                    bottom = 10.px
            )
        }
        dialogContainerActionsButtons {
            padding = box(
                    top = 10.px,
                    left = 0.px,
                    right = 0.px,
                    bottom = 0.px
            )
        }
        dialogHeader {
            fontSize = 24.pt
            fontFamily = "Verdana"
            padding = box(
                    top = 0.px,
                    left = 0.px,
                    right = 0.px,
                    bottom = 20.px
            )
        }
        sidePanelHeader {
            fontSize = 16.pt
            fontFamily = "Verdana"
            padding = box(
                    top = 0.px,
                    left = 0.px,
                    right = 0.px,
                    bottom = 20.px
            )
        }
        buttonMenu {
            val dimen = 46.px
            prefWidth = dimen
            prefHeight = dimen
            minWidth = dimen
            minHeight = dimen
            maxWidth = dimen
            maxHeight = dimen
            backgroundRadius.add(box(50.px))
            backgroundColor.add(cActiveRed)
            textFill = Color.WHITE
            ellipsisString = "..."
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
        buttonMenuMini {
            val dimen = 20.px
            prefWidth = dimen
            prefHeight = dimen
            minWidth = dimen
            minHeight = dimen
            maxWidth = dimen
            maxHeight = dimen
            backgroundRadius.add(box(50.px))
            backgroundColor.add(cActiveOrange)
            textFill = Color.WHITE
            ellipsisString = "..."
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
        inputTextField { }
        labelMini {
            textFill = Color.GRAY
            fontSize = 8.pt
        }
        labelRegular {
            textFill = Color.GRAY
            fontSize = 10.pt
        }
        popUpLabel {
            fontSize = 10.pt
        }
        emojiText {
            font = fontEmoji
            fontFamily = "OpenSansEmoji"
        }
        dialogButtonAction {
            jfxButtonType.value = JFXButton.ButtonType.RAISED
            backgroundColor.add(Color.WHITE)
            backgroundRadius.add(box(4.px))
        }
        textMini {
            textFill = Color.GRAY
            fontSize = 8.pt
        }
    }

}
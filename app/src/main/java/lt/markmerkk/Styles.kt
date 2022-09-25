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
        val dialogH1TextColorful by cssclass()
        val dialogH9TextColorful by cssclass()
        val dialogHeaderContainerColorful by cssclass()
        val sidePanelHeader by cssclass()
        val sidePanelContainer by cssclass()
        val dialogContainer by cssclass()
        val dialogContainerColorContent by cssclass()
        val dialogContainerActionsButtons by cssclass()
        val dialogContainerColorActionsButtons by cssclass()
        val dialogButtonAction by cssclass()
        val buttonMenu by cssclass()
        val buttonMenuMini by cssclass()
        val inputTextField by cssclass()
        val labelMini by cssclass()
        val labelRegular by cssclass()
        val popUpLabel by cssclass()
        val emojiText by cssclass()
        val textMini by cssclass()

        val dialogAlertContainer by cssclass()
        val dialogAlertContainerBig by cssclass()
        val dialogAlertContentContainer by cssclass()
        val dialogAlertTextH1 by cssclass()
        val dialogAlertTextRegular by cssclass()

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

        val cTextHeaderColorful = Color.WHITE
        val cTextMini = c("#FF6D00")

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
        dialogContainerColorContent {
            backgroundColor += Color.WHITE
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
        dialogContainerColorActionsButtons {
            backgroundColor += Color.WHITE
            padding = box(
                top = 10.px,
                left = 20.px,
                right = 20.px,
                bottom = 10.px
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
        dialogH1TextColorful {
            textFill = cTextHeaderColorful
            fontSize = 24.pt
            fontFamily = "Verdana"
        }
        dialogH9TextColorful {
            textFill = cTextHeaderColorful
            fontSize = 8.pt
            fontFamily = "Verdana"
        }
        dialogHeaderContainerColorful {
            backgroundColor += cActiveRed
            minHeight = 82.px
            maxHeight = 82.px
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
        dialogAlertContainer {
            minWidth = 320.px
            prefWidth = 320.px
            minHeight = 160.px
            prefHeight = 160.px
            padding = box(
                top = 10.px,
                left = 20.px,
                right = 20.px,
                bottom = 10.px
            )
        }
        dialogAlertContainerBig {
            minWidth = 420.px
            prefWidth = 420.px
            minHeight = 280.px
            prefHeight = 280.px
            padding = box(
                top = 10.px,
                left = 20.px,
                right = 20.px,
                bottom = 10.px
            )
        }
        dialogAlertContentContainer {
            padding = box(
                top = 10.px,
                left = 0.px,
                right = 0.px,
                bottom = 10.px
            )
        }
        dialogAlertTextH1 {
            fontSize = 24.pt
            fontFamily = "Verdana"
        }
        dialogAlertTextRegular {
            fontSize = 10.pt
            fontFamily = "Verdana"
            wrapText = true
        }
    }

}
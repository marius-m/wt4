package lt.markmerkk

import javafx.scene.control.OverrunStyle
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles: Stylesheet() {

    companion object {
        val buttonMenu by cssclass()
        val buttonMenuMini by cssclass()

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

        val cActivateRed = c("#E91E63")
        val cActivateOrange = Color.ORANGE
        val cBackgroundPrimary = c("#5C6BC0")
    }

    init {
        buttonMenu {
            val dimen = 46.px
            prefWidth = dimen
            prefHeight = dimen
            minWidth = dimen
            minHeight = dimen
            maxWidth = dimen
            maxHeight = dimen
            backgroundRadius.add(box(50.px))
            backgroundColor.add(cActivateRed)
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
            backgroundColor.add(cActivateOrange)
            textFill = Color.WHITE
            ellipsisString = "..."
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
    }

}
package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.fxml.FXML
import javafx.fxml.Initializable
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Strings
import lt.markmerkk.Tags
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import org.slf4j.LoggerFactory
import tornadofx.Stylesheet.Companion.label
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class TicketSplitController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout

    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var strings: Strings

    private lateinit var logToSplit: SimpleLog
    private lateinit var widgetTicketSplit: TicketSplitWidget
    private val dialogPadding = 160.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        logToSplit = resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)!!

        // Views
        widgetTicketSplit = TicketSplitWidget(strings, jfxDialog)
        jfxDialogLayout.setHeading(widgetTicketSplit.header)
        jfxDialogLayout.setBody(widgetTicketSplit.root)
        jfxDialogLayout.setActions(widgetTicketSplit.actions)
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding
        stageProperties.register(stageChangeListener)
    }

    @PreDestroy
    fun destroy() {
        stageProperties.unregister(stageChangeListener)
    }

    private fun initDialogContent() {
    }

    private val stageChangeListener = object : StageProperties.StageChangeListener {
        override fun onNewWidth(newWidth: Double) {
            jfxDialogLayout.prefWidth = newWidth - dialogPadding
        }

        override fun onNewHeight(newHeight: Double) {
            jfxDialogLayout.prefHeight = newHeight - dialogPadding
        }

        override fun onFocusChange(focus: Boolean) {}
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKET_SPLIT)
        const val RESULT_DISPATCH_KEY_ENTITY = "EIE5nv2wNk"
    }

}
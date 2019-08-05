package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitPresenter
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import lt.markmerkk.utils.LogSplitter
import org.slf4j.LoggerFactory
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
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var schedulerProvider: SchedulerProvider

    private lateinit var widgetTicketSplit: TicketSplitWidget
    private val dialogPadding = 100.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component().presenterComponent().inject(this)

        // Views
        widgetTicketSplit = TicketSplitWidget(
                strings,
                graphics,
                jfxDialog,
                TicketSplitPresenter(
                        resultDispatcher.consume(RESULT_DISPATCH_KEY_ENTITY, SimpleLog::class.java)!!,
                        timeProvider,
                        logStorage,
                        LogSplitter,
                        strings,
                        ticketsDatabaseRepo,
                        schedulerProvider
                )
        )
        jfxDialogLayout.setHeading(widgetTicketSplit.header)
        jfxDialogLayout.setBody(widgetTicketSplit.root)
        jfxDialogLayout.setActions(widgetTicketSplit.actions)
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding
        stageProperties.register(stageChangeListener)
        widgetTicketSplit.onAttach()
    }

    @PreDestroy
    fun destroy() {
        widgetTicketSplit.onDetach()
        stageProperties.unregister(stageChangeListener)
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
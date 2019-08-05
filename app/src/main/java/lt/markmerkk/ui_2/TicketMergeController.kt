package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui_2.views.ticket_merge.TicketMergePresenter
import lt.markmerkk.ui_2.views.ticket_merge.TicketMergeWidget
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitPresenter
import lt.markmerkk.ui_2.views.ticket_split.TicketSplitWidget
import lt.markmerkk.utils.LogSplitter
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class TicketMergeController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout

    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var timeProvider: TimeProvider

    private lateinit var widgetTicketMerge: TicketMergeWidget
    private val dialogPadding = 100.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component().presenterComponent().inject(this)

        // Views
        widgetTicketMerge = TicketMergeWidget(
                strings,
                graphics,
                jfxDialog,
                TicketMergePresenter()
        )
        jfxDialogLayout.setHeading(widgetTicketMerge.header)
        jfxDialogLayout.setBody(widgetTicketMerge.root)
        jfxDialogLayout.setActions(widgetTicketMerge.actions)
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding
        stageProperties.register(stageChangeListener)
        widgetTicketMerge.onAttach()
    }

    @PreDestroy
    fun destroy() {
        widgetTicketMerge.onDetach()
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
        private val logger = LoggerFactory.getLogger(Tags.TICKET_MERGE)
        const val RESULT_DISPATCH_KEY_ENTITY = ""
    }

}
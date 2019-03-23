package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.StackPane
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.Ticket
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketsRepository
import lt.markmerkk.ui_2.adapters.TicketListAdapter
import lt.markmerkk.ui_2.adapters.TicketViewItem
import org.slf4j.LoggerFactory
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

class TicketsController : Initializable {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonDismiss: JFXButton
    @FXML lateinit var jfxTextFieldTicketSearch: JFXTextField
    @FXML lateinit var jfxTable: JFXTreeTableView<TicketViewItem>

    @Inject lateinit var ticketsRepository: TicketsRepository
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    lateinit var ticketsListAdaper: TicketListAdapter
    lateinit var ticketLoader: TicketLoader

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        // Views
        val dialogPadding = 100.0
        stageProperties.propertyWidth.addListener { _, _, newValue ->
            jfxDialogLayout.prefWidth = newValue.toDouble() - dialogPadding
        }
        stageProperties.propertyHeight.addListener { _, _, newValue ->
            jfxDialogLayout.prefHeight = newValue.toDouble() - dialogPadding
        }
        jfxDialogLayout.prefWidth = stageProperties.propertyWidth.get() - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.propertyHeight.get() - dialogPadding
        ticketsListAdaper = TicketListAdapter(jfxDialogLayout, jfxTable, graphics)
        jfxTextFieldTicketSearch.textProperty().addListener { _, _, newValue ->
            ticketLoader.search(newValue)
        }

        // Loaders
        ticketLoader = TicketLoader(
                listener = object : TicketLoader.Listener {
                    override fun onTicketsReady(tickets: List<Ticket>) {
                        ticketsListAdaper.renewTickets(tickets)
                    }

                    override fun onNoTickets() {
                        ticketsListAdaper.renewTickets(emptyList())
                    }

                    override fun onError(throwable: Throwable) {
                        logger.error("Error", throwable)
                    }

                },
                ticketsRepository = ticketsRepository,
                timeProvider = timeProvider,
                ioScheduler = Schedulers.io(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        ticketLoader.onAttach()
        ticketLoader.loadTickets()
    }

    @PostConstruct
    fun afterLoad() {
        logger.debug("On attach")
    }

    @PreDestroy
    fun destroy() {
        logger.debug("On detach")
        ticketLoader.onDetach()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }

}
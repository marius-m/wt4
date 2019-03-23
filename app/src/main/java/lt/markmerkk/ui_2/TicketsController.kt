package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.StackPane
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketsNetworkRepo
import lt.markmerkk.ui_2.adapters.TicketListAdapter
import lt.markmerkk.ui_2.adapters.TicketViewItem
import lt.markmerkk.ui_2.bridges.UIEProgressView
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
    @FXML lateinit var jfxContainerContentRefresh: StackPane
    @FXML lateinit var jfxSpinnerProgress: JFXSpinner
    @FXML lateinit var jfxButtonProgressRefresh: JFXButton
    @FXML lateinit var jfxButtonProgressStop: JFXButton

    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var eventBus: EventBus

    lateinit var ticketsListAdaper: TicketListAdapter
    lateinit var ticketLoader: TicketLoader
    lateinit var uieProgressView: UIEProgressView

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        // Views
        val dialogPadding = 160.0
        stageProperties.propertyWidth.addListener { _, _, newValue ->
            jfxDialogLayout.prefWidth = newValue.toDouble() - dialogPadding
        }
        stageProperties.propertyHeight.addListener { _, _, newValue ->
            jfxDialogLayout.prefHeight = newValue.toDouble() - dialogPadding
        }
        jfxDialogLayout.prefWidth = stageProperties.propertyWidth.get() - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.propertyHeight.get() - dialogPadding
        ticketsListAdaper = TicketListAdapter(
                listener = object : TicketListAdapter.Listener {
                    override fun onTicketSelect(ticket: Ticket) {
                        eventBus.post(EventSuggestTicket(ticket))
                        jfxDialog.close()
                    }
                },
                jfxDialogLayout = jfxDialogLayout,
                jfxTable = jfxTable,
                graphics = graphics
        )
        jfxTextFieldTicketSearch.textProperty().addListener { _, _, newValue -> ticketLoader.applyFilter(newValue) }
        uieProgressView = UIEProgressView(
                jfxContainerContentRefresh,
                jfxButtonProgressRefresh,
                jfxButtonProgressStop,
                jfxSpinnerProgress,
                graphics,
                refreshListener = object : UIEProgressView.RefreshListener {
                    override fun onClickRefresh() {
                        ticketLoader.fetchTickets(forceRefresh = true)
                    }

                    override fun onClickStop() {
                        uieProgressView.hide()
                        ticketLoader.stopFetch()
                    }
                }
        )

        // Loaders
        ticketLoader = TicketLoader(
                listener = object : TicketLoader.Listener {
                    override fun onLoadStart() {
                        uieProgressView.show()
                    }

                    override fun onLoadFinish() {
                        uieProgressView.hide()
                    }

                    override fun onNewTickets(tickets: List<Ticket>) { }

                    override fun onTicketsAvailable(tickets: List<Ticket>) {
                        ticketsListAdaper.renewTickets(tickets)
                    }

                    override fun onNoTickets() {
                        ticketsListAdaper.renewTickets(emptyList())
                    }

                    override fun onError(throwable: Throwable) {
                        logger.error("Error", throwable)
                    }

                },
                ticketsDatabaseRepo = ticketsDatabaseRepo,
                ticketsNetworkRepo = ticketsNetworkRepo,
                timeProvider = timeProvider,
                userSettings = userSettings,
                ioScheduler = Schedulers.io(),
                uiScheduler = JavaFxScheduler.getInstance()
        )
        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        ticketLoader.onAttach()
        ticketLoader.fetchTickets()
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
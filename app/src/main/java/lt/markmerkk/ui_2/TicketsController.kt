package lt.markmerkk.ui_2

import com.google.common.eventbus.EventBus
import com.jfoenix.controls.*
import com.jfoenix.svg.SVGGlyph
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.tickets.TicketLoader
import lt.markmerkk.tickets.TicketsNetworkRepo
import lt.markmerkk.ui_2.adapters.TicketListAdapter
import lt.markmerkk.ui_2.adapters.TicketViewItem
import lt.markmerkk.ui_2.bridges.UIEProgressView
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

@Deprecated("Replaced with TicketWidget")
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
    @FXML lateinit var jfxButtonClear: JFXButton

    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var eventBus: EventBus
    @Inject lateinit var schedulerProvider: SchedulerProvider

    lateinit var ticketsListAdaper: TicketListAdapter
    lateinit var ticketLoader: TicketLoader
    lateinit var uieProgressView: UIEProgressView

    private val dialogPadding = 160.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component().presenterComponent().inject(this)

        // Views
        jfxDialogLayout.prefWidth = stageProperties.width - dialogPadding
        jfxDialogLayout.prefHeight = stageProperties.height - dialogPadding
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
        jfxTextFieldTicketSearch.textProperty().addListener { _, _, newValue ->
            if (newValue.isEmpty()) {
                jfxButtonClear.isVisible = false
                jfxButtonClear.isManaged = false
            } else {
                jfxButtonClear.isVisible = true
                jfxButtonClear.isManaged = true
            }
        }
        jfxButtonClear.setOnAction { jfxTextFieldTicketSearch.text = "" }
        jfxButtonClear.isVisible = false
        jfxButtonClear.isManaged = false
        jfxButtonClear.graphic = graphics.from(Glyph.CLEAR, Color.BLACK, 12.0)

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
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        ticketLoader.onAttach()
        ticketLoader.fetchTickets()
        ticketLoader.loadTickets()
        stageProperties.register(stageChangeListener)
    }

    @PreDestroy
    fun destroy() {
        stageProperties.unregister(stageChangeListener)
        ticketLoader.onDetach()
    }

    private val stageChangeListener = object : StageProperties.StageChangeListener {
        override fun onNewWidth(newWidth: Double) {
            jfxDialogLayout.prefWidth = newWidth - dialogPadding
        }

        override fun onNewHeight(newHeight: Double) {
            jfxDialogLayout.prefHeight = newHeight - dialogPadding
        }

        override fun onFocusChange(focus: Boolean) { }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.TICKETS)
    }

}
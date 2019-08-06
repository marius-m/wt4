package lt.markmerkk.widgets.tickets

import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import lt.markmerkk.*
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.tickets.TicketsNetworkRepo
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxTextField
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class TicketWidget: View(), TicketContract.View {

    @Inject lateinit var ticketsDatabaseRepo: TicketsDatabaseRepo
    @Inject lateinit var ticketsNetworkRepo: TicketsNetworkRepo
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var eventBus: com.google.common.eventbus.EventBus
    @Inject lateinit var schedulerProvider: SchedulerProvider

    init {
        Main.component().inject(this)
    }

    private lateinit var viewTextFieldTicketSearch: JFXTextField
    private lateinit var viewProgress: TicketProgressWidget
    private lateinit var viewTable: TableView<TicketViewModel>

    private val presenter: TicketContract.Presenter = TicketPresenter(
            ticketsDatabaseRepo,
            ticketsNetworkRepo,
            timeProvider,
            userSettings,
            schedulerProvider
    )
    private val ticketViewModels = mutableListOf<TicketViewModel>()
            .observable()

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            label("Tickets") {
                addClass(Styles.dialogHeader)
            }
        }
        center {
            vbox(spacing = 4) {
                hbox(spacing = 4) {
                    viewTextFieldTicketSearch = jfxTextField {
                        hgrow = Priority.ALWAYS
                        addClass(Styles.inputTextField)
                        focusColor = Styles.cActiveRed
                        isLabelFloat = true
                        promptText = "Search ticket by ID / Summary"
                        textProperty().addListener { _, _, newValue -> presenter.applyFilter(newValue) }
                    }
                    viewProgress = find<TicketProgressWidget>()
                    viewProgress.viewButtonStop.setOnAction {
                        logger.debug("Trigger stop fetch")
                        presenter.stopFetch()
                    }
                    viewProgress.viewButtonRefresh.setOnAction {
                        logger.debug("Trigger fetching ")
                        presenter.fetchTickets()
                    }
                    add(viewProgress)
                }
                viewTable = tableview(ticketViewModels) {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    setOnMouseClicked { mouseEvent ->
                        val selectTicket = viewTable.selectionModel.selectedItems.firstOrNull()?.ticket
                        if (mouseEvent.clickCount >= 2 && selectTicket != null) {
                            eventBus.post(EventSuggestTicket(selectTicket))
                            close()
                        }
                    }
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    readonlyColumn("Code", TicketViewModel::code) {
                        minWidth = 100.0
                        maxWidth = 100.0
                    }
                    readonlyColumn("Description", TicketViewModel::description) {
                    }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Dismiss") {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter.onAttach(this)
        presenter.loadTickets()
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
    }

    override fun onTicketUpdate(tickets: List<TicketViewModel>) {
        this.ticketViewModels.clear()
        this.ticketViewModels.addAll(tickets)
    }

    override fun showProgress() {
        viewProgress.changeProgressActive()
    }

    override fun hideProgress() {
        viewProgress.changeProgressInactive()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TicketWidget::class.java)!!
    }

}
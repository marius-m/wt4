package lt.markmerkk.widgets.tickets

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxTextField
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

class TicketWidget: View(), TicketContract.View {

    @Inject lateinit var ticketStorage: TicketStorage
    @Inject lateinit var ticketApi: TicketApi
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
    private lateinit var viewButtonClear: JFXButton

    private lateinit var presenter: TicketContract.Presenter
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
                    }
                    viewButtonClear = jfxButton {
                        graphic = graphics.from(Glyph.CLEAR, Color.BLACK, 12.0)
                        setOnAction { presenter.clearFilter() }
                    }
                    viewProgress = find<TicketProgressWidget>()
                    viewProgress.viewButtonStop.setOnAction {
                        logger.debug("Trigger stop fetch")
                        presenter.stopFetch()
                    }
                    viewProgress.viewButtonRefresh.setOnAction {
                        logger.debug("Trigger fetching ")
                        presenter.fetchTickets(forceFetch = true, filter = viewTextFieldTicketSearch.text)
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
                    readonlyColumn("Score", TicketViewModel::filterScoreAsDouble) {
                        minWidth = 50.0
                        maxWidth = 50.0
                        useProgressBar()
                    }
                    readonlyColumn("Code", TicketViewModel::code) {
                        minWidth = 100.0
                        maxWidth = 100.0
                    }
                    readonlyColumn("Description", TicketViewModel::description) { }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Dismiss".toUpperCase()) {
                    setOnAction {
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter = TicketPresenter(
                ticketStorage,
                ticketApi,
                timeProvider,
                userSettings,
                schedulerProvider
        )
        presenter.attachFilterStream(JavaFxObservable.valuesOf(viewTextFieldTicketSearch.textProperty()))
        presenter.onAttach(this)
        presenter.fetchTickets(forceFetch = false, filter = "")
        presenter.loadTickets(viewTextFieldTicketSearch.text)
        viewTextFieldTicketSearch.requestFocus()
        viewTextFieldTicketSearch.positionCaret(viewTextFieldTicketSearch.text.length)
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

    override fun showInputClear() {
        viewButtonClear.show()
    }

    override fun hideInputClear() {
        viewButtonClear.hide()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TicketWidget::class.java)!!
    }

}
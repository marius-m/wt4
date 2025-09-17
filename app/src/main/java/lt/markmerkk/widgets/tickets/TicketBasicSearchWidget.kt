package lt.markmerkk.widgets.tickets

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.entities.Ticket
import lt.markmerkk.events.EventMainCloseTickets
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.tickets.TicketLoaderBasic
import lt.markmerkk.ui_2.views.ContextMenuTicketSelect
import lt.markmerkk.ui_2.views.cfxPrefixSelectionComboBox
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxTextField
import lt.markmerkk.utils.AccountAvailablility
import org.controlsfx.control.PrefixSelectionComboBox
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class TicketBasicSearchWidget: BaseFragment(), TicketLoaderBasic.Listener {

    @Inject lateinit var ticketStorage: TicketStorage
    @Inject lateinit var ticketApi: TicketApi
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var accountAvailablility: AccountAvailablility

    init {
        Main.component().inject(this)
    }

    private lateinit var viewTextFieldTicketSearch: JFXTextField
    private lateinit var viewProgress: TicketProgressWidget
    private lateinit var viewTable: TableView<TicketViewModelBasic>
    private lateinit var viewButtonClear: JFXButton

    private val ticketViewModels = mutableListOf<TicketViewModelBasic>()
            .asObservable()
    private val contextMenuTicketSelect: ContextMenuTicketSelect = ContextMenuTicketSelect(
            graphics = graphics,
            eventBus = eventBus,
            hostServicesInteractor = hostServicesInteractor,
            accountAvailablility = accountAvailablility
    )
    private lateinit var ticketLoaderBasic: TicketLoaderBasic

    override val root: Parent = borderpane {
//        setOnKeyReleased { keyEvent ->
//            if (keyEvent.code == KeyCode.ENTER) {
//                val selectTicket = viewTable.selectionModel.selectedItems.firstOrNull()?.ticket
//                if (selectTicket != null) {
//                    eventBus.post(EventSuggestTicket(selectTicket))
//                    eventBus.post(EventMainCloseTickets())
//                }
//            }
//        }
        addClass(Styles.sidePanelContainer)
        top {
            label("Tickets") {
                addClass(Styles.sidePanelHeader)
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
                        promptText = "Ticket search by project code or description"
                    }
                    viewButtonClear = jfxButton {
                        graphic = graphics.from(Glyph.CLEAR, Color.BLACK, 12.0)
                        setOnAction {
                            viewTextFieldTicketSearch.text = ""
                            viewTextFieldTicketSearch.requestFocus()
                        }
                        isFocusTraversable = false
                    }
                    viewProgress = find<TicketProgressWidget>() {}
                    viewProgress.viewButtonStop.setOnAction {
//                        TicketSideDrawerWidget.logger.debug("Trigger stop fetch")
//                        presenter.stopFetch()
                    }
                    viewProgress.viewButtonStop.isFocusTraversable = false
                    viewProgress.viewButtonRefresh.setOnAction {
//                        TicketSideDrawerWidget.logger.debug("Trigger fetching ")
//                        presenter.fetchTickets(
//                                forceFetch = true,
//                                filter = viewTextFieldTicketSearch.text,
//                                projectCode = viewComboProjectCodes.selectionModel.selectedItem ?: ""
//                        )
                    }
                    viewProgress.viewButtonRefresh.isFocusTraversable = false
                    add(viewProgress)
                }
                viewTable = tableview(ticketViewModels) {
                    contextMenu = contextMenuTicketSelect.root
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                    setOnMouseClicked { mouseEvent ->
                        val selectTicket = viewTable.selectionModel.selectedItems.firstOrNull()?.ticket
                        if (mouseEvent.clickCount >= 2 && selectTicket != null) {
                            eventBus.post(EventSuggestTicket(selectTicket))
                            eventBus.post(EventMainCloseTickets())
                        }
                    }
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    readonlyColumn("Description", TicketViewModelBasic::description) { }
                }
                label("For more options - press secondary button on the ticket") {
                    addClass(Styles.labelMini)
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Filter".toUpperCase()) {
                    setOnAction {
                        find<TicketFilterSettingsWidget>()
                                .openModal()
                    }
                }
                jfxButton("Close".toUpperCase()) {
                    setOnAction {
                        eventBus.post(EventMainCloseTickets())
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        ticketLoaderBasic = TicketLoaderBasic(
                listener = this,
                ticketStorage = ticketStorage,
                ticketApi = ticketApi,
                timeProvider = timeProvider,
                userSettings = userSettings,
                ioScheduler = schedulerProvider.io(),
                uiScheduler = schedulerProvider.ui()
        )
        ticketLoaderBasic.onAttach()
        val filterChangeStream = JavaFxObservable.valuesOf(viewTextFieldTicketSearch.textProperty())
        ticketLoaderBasic
                .changeFilterStream(filterChangeStream)
        ticketLoaderBasic.loadTickets(inputFilter = "")
    }

    override fun onUndock() {
        ticketLoaderBasic.onDetach()
        super.onUndock()
    }

    override fun onLoadStart() { }

    override fun onLoadFinish() { }

    override fun onFoundTickets(tickets: List<Ticket>) {
        ticketViewModels.clear()
        val ticketVms = tickets
                .map { TicketViewModelBasic(it) }
        ticketViewModels.addAll(ticketVms)
    }

    override fun onNoTickets() {
        ticketViewModels.clear()
    }

    override fun onError(throwable: Throwable) {
        ticketViewModels.clear()
    }

}
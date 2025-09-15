package lt.markmerkk.widgets.tickets

import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import com.jfoenix.svg.SVGGlyph
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.*
import lt.markmerkk.events.EventFocusTicketWidget
import lt.markmerkk.events.EventMainCloseTickets
import lt.markmerkk.events.EventSuggestTicket
import lt.markmerkk.events.EventTicketFilterChange
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.ui_2.views.ContextMenuTicketSelect
import lt.markmerkk.ui_2.views.cfxPrefixSelectionComboBox
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxTextField
import lt.markmerkk.utils.AccountAvailablility
import org.controlsfx.control.PrefixSelectionComboBox
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class TicketSideDrawerWidget: BaseFragment(), TicketContract.View {

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

    private lateinit var viewComboProjectCodes: PrefixSelectionComboBox<String>
    private lateinit var viewTextFieldTicketSearch: JFXTextField
    private lateinit var viewProgress: TicketProgressWidget
    private lateinit var viewTable: TableView<TicketViewModel>
    private lateinit var viewButtonClear: JFXButton
    private lateinit var viewButtonFilter: JFXButton

    private lateinit var presenter: TicketContract.Presenter
    private val ticketViewModels = mutableListOf<TicketViewModel>()
            .asObservable()
    private val projectCodes = mutableListOf<String>()
            .asObservable()
    private val contextMenuTicketSelect: ContextMenuTicketSelect = ContextMenuTicketSelect(
            graphics = graphics,
            eventBus = eventBus,
            hostServicesInteractor = hostServicesInteractor,
            accountAvailablility = accountAvailablility
    )

    override val root: Parent = borderpane {
        setOnKeyReleased { keyEvent ->
            if (keyEvent.code == KeyCode.ENTER) {
                if (viewTable.isFocused) {
                    val selectTicket = viewTable
                            .selectionModel
                            .selectedItems
                            .firstOrNull()?.ticket
                    if (selectTicket != null) {
                        eventBus.post(EventSuggestTicket(selectTicket))
                        eventBus.post(EventMainCloseTickets())
                    }
                }
            }
        }
        addClass(Styles.sidePanelContainer)
        top {
            label("Tickets") {
                addClass(Styles.sidePanelHeader)
            }
        }
        center {
            vbox(spacing = 4) {
                hbox(spacing = 4) {
                    viewComboProjectCodes = cfxPrefixSelectionComboBox(SimpleStringProperty(""), projectCodes) {
                        setOnAction {
                            val selectItem = (it.source as PrefixSelectionComboBox<String>)
                                    .selectionModel
                                    .selectedItem ?: ""
                            logger.debug("Making selecting in $selectItem")
                            presenter.loadTickets(
                                    filter = viewTextFieldTicketSearch.text,
                                    projectCode = selectItem
                            )
                        }
                        isDisplayOnFocusedEnabled = true
                        typingDelay = 1000
                        isBackSpaceAllowed = true
                    }
                    viewTextFieldTicketSearch = jfxTextField {
                        hgrow = Priority.ALWAYS
                        addClass(Styles.inputTextField)
                        focusColor = Styles.cActiveRed
                        isLabelFloat = true
                        promptText = "Search by ticket description"
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
                        logger.debug("Trigger stop fetch")
                        presenter.stopFetch()
                    }
                    viewProgress.viewButtonStop.isFocusTraversable = false
                    viewProgress.viewButtonRefresh.setOnAction {
                        logger.debug("Trigger fetching ")
                        presenter.fetchTickets(
                                forceFetch = true,
                                filter = viewTextFieldTicketSearch.text,
                                projectCode = viewComboProjectCodes.selectionModel.selectedItem ?: ""
                        )
                    }
                    viewProgress.viewButtonRefresh.isFocusTraversable = false
                    add(viewProgress)
                    viewButtonFilter = jfxButton {
                        graphic = graphics.from(Glyph.FILTER, Color.BLACK, 14.0, 12.0)
                        action {
                            find<TicketFilterSettingsWidget>()
                                    .openModal()
                        }
                        isFocusTraversable = false
                    }
                }
                label("Search tickets") {
                    addClass(Styles.labelMini)
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
                label("For more options - press secondary button on the ticket") {
                    addClass(Styles.labelMini)
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
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
        logger.debug("TicketDrawer:onDock()")
        presenter = TicketPresenter(
                ticketStorage,
                ticketApi,
                timeProvider,
                userSettings,
                schedulerProvider
        )
        viewTextFieldTicketSearch.text = ""
        presenter.onAttach(this)
        presenter.fetchTickets(forceFetch = false, filter = "", projectCode = "")
        presenter.loadTickets(filter = "", projectCode = "")
        presenter.attachFilterStream(JavaFxObservable.valuesOf(viewTextFieldTicketSearch.textProperty()))
        JavaFxObservable.valuesOf(viewTextFieldTicketSearch.textProperty())
                .subscribe({
                    presenter.handleClearVisibility(it)
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        viewComboProjectCodes.selectionModel.select("")
        presenter.loadProjectCodes()
        contextMenuTicketSelect.onAttach()
        contextMenuTicketSelect.attachTicketSelection(
                JavaFxObservable.valuesOf(viewTable.selectionModel.selectedItemProperty())
                        .filter { it != null }
                        .map { it.code }
        )
        eventBus.register(this)
        Platform.runLater {
            // todo restore old search name
            viewTextFieldTicketSearch.requestFocus()
        }
    }

    override fun onUndock() {
        logger.debug("TicketDrawer:onUndock()")
        eventBus.unregister(this)
        contextMenuTicketSelect.onDetach()
        presenter.onDetach()
        ticketViewModels.clear()
        projectCodes.clear()
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

    override fun onProjectCodes(projectCodes: List<String>) {
        this.projectCodes.clear()
        this.projectCodes.addAll(projectCodes)
    }

    //region events

    @Subscribe
    fun onFocusTicketWidget(event: EventFocusTicketWidget) {
        viewTextFieldTicketSearch.requestFocus()
        viewTextFieldTicketSearch.positionCaret(viewTextFieldTicketSearch.text.length)
    }

    @Subscribe
    fun onTicketFilterChange(event: EventTicketFilterChange) {
        val projectCodeItem = viewComboProjectCodes
                .selectionModel
                .selectedItem ?: ""
        presenter.loadTickets(
                filter = viewTextFieldTicketSearch.text,
                projectCode = projectCodeItem
        )
    }

    //endregion

    companion object {
        private val logger = LoggerFactory.getLogger(TicketSideDrawerWidget::class.java)!!
    }

}
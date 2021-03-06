package lt.markmerkk.widgets.tickets

import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.svg.SVGGlyph
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.TableView
import javafx.scene.layout.VBox
import lt.markmerkk.*
import lt.markmerkk.entities.TicketStatus
import lt.markmerkk.events.EventTicketFilterChange
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.TicketApi
import lt.markmerkk.tickets.TicketStatusesLoader
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSpinner
import lt.markmerkk.utils.AccountAvailablility
import tornadofx.*
import javax.inject.Inject

class TicketFilterSettingsWidget: Fragment(), TicketFilterSettingsContract.View {

    @Inject lateinit var ticketStorage: TicketStorage
    @Inject lateinit var ticketApi: TicketApi
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var userSettings: UserSettings
    @Inject lateinit var eventBus: com.google.common.eventbus.EventBus
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var hostServicesInteractor: HostServicesInteractor
    @Inject lateinit var accountAvailablility: AccountAvailablility

    private lateinit var viewContainerMain: VBox
    private lateinit var viewProgress: JFXSpinner
    private lateinit var viewStatusList: TableView<TicketStatusViewModel>
    private lateinit var viewOnlyCurrentUser: CheckBox
    private lateinit var viewFilterIncludeAssignee: CheckBox
    private lateinit var viewFilterIncludeReporter: CheckBox
    private lateinit var viewFilterIncludeIsWatching: CheckBox

    private lateinit var presenter: TicketFilterSettingsContract.Presenter

    private val ticketStatusViewModels = mutableListOf<TicketStatusViewModel>()
            .asObservable()

    init {
        Main.component().inject(this)
    }

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        style {
            minHeight = 300.0.px
        }
        top {
            label("Ticket filter") {
                addClass(Styles.dialogHeader)
            }
        }
        center {
            stackpane {
                viewContainerMain = vbox(spacing = 4) {
                    viewFilterIncludeAssignee = checkbox("Include assigned tickets") {
                        isSelected = userSettings.ticketFilterIncludeAssignee
                    }
                    viewFilterIncludeReporter = checkbox("Include reported tickets") {
                        isSelected = userSettings.ticketFilterIncludeReporter
                    }
                    viewFilterIncludeIsWatching = checkbox("Include watching tickets") {
                        isSelected = userSettings.ticketFilterIncludeIsWatching
                    }
                    viewStatusList = tableview(ticketStatusViewModels) {
                        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                        column("Status", TicketStatusViewModel::nameProperty) { }
                        column("Enabled", TicketStatusViewModel::enableProperty)
                                .useCheckbox()
                    }
//                    viewOnlyCurrentUser = checkbox("Only current user tickets") {
//                        isSelected = userSettings.onlyCurrentUserIssues
//                    }
                }
                viewProgress = jfxSpinner {
                    style {
                        padding = box(2.0.px)
                    }
                    minWidth = 24.0
                    minHeight = 24.0
                    prefWidth = 24.0
                    prefHeight = 24.0
                    maxWidth = 24.0
                    maxHeight = 24.0
                    hide()
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Save and exit".toUpperCase()) {
                    setOnAction {
                        presenter.saveTicketStatuses(
                                ticketStatusViewModels = ticketStatusViewModels,
                                useOnlyCurrentUser = true, // todo disable user selection to display all tickets
                                filterIncludeAssignee = viewFilterIncludeAssignee.isSelected,
                                filterIncludeReporter = viewFilterIncludeReporter.isSelected,
                                filterIncludeIsWatching = viewFilterIncludeIsWatching.isSelected
                        )
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter = TicketFilterSettingsPresenter(
                this,
                ticketApi,
                timeProvider,
                ticketStorage,
                userSettings,
                schedulerProvider
        )
        presenter.onAttach()
        presenter.loadTicketStatuses()
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
    }

    override fun showProgress() {
        viewContainerMain.hide()
        viewProgress.show()
    }

    override fun hideProgress() {
        viewContainerMain.show()
        viewProgress.hide()
    }

    override fun showStatuses(statuses: List<TicketStatus>) {
        val statusesAsViewModels = statuses
                .map { TicketStatusViewModel(it.name, it.enabled) }
        ticketStatusViewModels.clear()
        ticketStatusViewModels.addAll(statusesAsViewModels)
    }

    override fun noStatuses() {
        ticketStatusViewModels.clear()
    }

    override fun cleanUpAndExit() {
        eventBus.post(EventTicketFilterChange())
        close()
    }

}

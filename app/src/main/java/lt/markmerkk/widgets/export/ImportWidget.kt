package lt.markmerkk.widgets.export

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Strings
import lt.markmerkk.Styles
import lt.markmerkk.TimeProvider
import lt.markmerkk.WorklogStorage
import lt.markmerkk.export.ImportContract
import lt.markmerkk.export.ImportPresenter
import lt.markmerkk.export.WorklogExporter
import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.Logs.withLogInstance
import lt.markmerkk.widgets.dialogs.Dialogs
import lt.markmerkk.widgets.export.tableview.ImportTableCell
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.box
import tornadofx.center
import tornadofx.checkbox
import tornadofx.column
import tornadofx.combobox
import tornadofx.hbox
import tornadofx.label
import tornadofx.px
import tornadofx.readonlyColumn
import tornadofx.style
import tornadofx.tableview
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class ImportWidget : BaseFragment(), ImportContract.View {

    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var worklogExporter: WorklogExporter
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository
    @Inject lateinit var dialogs: Dialogs
    @Inject lateinit var strings: Strings

    init {
        Main.component().inject(this)
    }

    private lateinit var presenter: ImportContract.Presenter

    private lateinit var viewLogs: TableView<ExportWorklogViewModel>
    private lateinit var viewCheckNoChanges: CheckBox
    private lateinit var viewCheckNoTicketCode: CheckBox
    private lateinit var viewCheckTicketCodeFromComment: CheckBox
    private lateinit var viewCheckTicketCodeAndRemoveFromComment: CheckBox
    private lateinit var viewProjectFilters: ComboBox<String>
    private lateinit var viewTotal: Label

    private val worklogViewModels = mutableListOf<ExportWorklogViewModel>().asObservable()
    private val projectFilters = mutableListOf<String>().asObservable()
    private val importFilters = ImportFilters()

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Import worklogs") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            vbox(spacing = 4.0) {
                label("Changes to worklog") {
                    style {
                        padding = box(
                            top = 10.px,
                            left = 0.px,
                            right = 0.px,
                            bottom = 0.px
                        )
                    }
                }
                viewCheckNoChanges = checkbox("No changes to ticket code") {
                    setOnAction {
                        renderByImportFilterResult(
                            filterResult = importFilters
                                .filter(action = IFActionClear)
                        )
                    }
                }
                viewCheckNoTicketCode = checkbox("Clear ticket code") {
                    setOnAction {
                        renderByImportFilterResult(
                            filterResult = importFilters
                                .filter(action = IFActionNoTicketCode)
                        )
                    }
                }
                viewCheckTicketCodeFromComment = checkbox("Ticket code from comment") {
                    setOnAction {
                        renderByImportFilterResult(
                            filterResult = importFilters
                                .filter(action = IFActionTicketCodeFromComments)
                        )
                    }
                }
                viewCheckTicketCodeAndRemoveFromComment = checkbox("Ticket code from comment (And remove)") {
                    setOnAction {
                        renderByImportFilterResult(
                            filterResult = importFilters
                                .filter(action = IFActionTicketCodeAndRemoveFromComment)
                        )
                    }
                }
                label("Worklog filter") {
                    style {
                        padding = box(
                            top = 10.px,
                            left = 0.px,
                            right = 0.px,
                            bottom = 0.px
                        )
                    }
                }
                hbox(spacing = 4) {
                    viewProjectFilters = combobox(SimpleStringProperty(""), projectFilters) {
                        setOnAction {
                            renderByImportFilterResult(
                                filterResult = importFilters.filter(importFilters.lastAction)
                            )
                        }
                    }
                }
                label("Tickets") {
                    style {
                        padding = box(
                            top = 10.px,
                            left = 0.px,
                            right = 0.px,
                            bottom = 0.px
                        )
                    }
                }
                viewLogs = tableview(worklogViewModels) {
                    prefHeight = 200.0
                    vgrow = Priority.ALWAYS
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    column("x", ExportWorklogViewModel::selectedProperty) {
                        minWidth = 32.0
                        maxWidth = 32.0
                        cellFragment(ImportTableCell::class)
                    }
                    readonlyColumn("Date", ExportWorklogViewModel::date) {
                        minWidth = 80.0
                        maxWidth = 80.0
                    }
                    readonlyColumn("Ticket", ExportWorklogViewModel::ticket) {
                        minWidth = 100.0
                        maxWidth = 100.0
                    }
                    readonlyColumn("Comment", ExportWorklogViewModel::comment)
                }
                viewTotal = label {
                    addClass(Styles.labelMini)
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Load".toUpperCase()) {
                    setOnAction {
                        val importWorklogs = worklogExporter.importFromFile()
                        presenter.loadWorklogs(
                                importWorklogs = importWorklogs,
                                projectFilter = presenter.defaultProjectFilter
                        )
                        renderByImportFilterResult(
                            filterResult = importFilters
                                .filter(action = IFActionClear)
                        )
                    }
                }
                jfxButton("Import".toUpperCase()) {
                    setOnAction {
                        presenter.import(worklogViewModels, viewCheckNoTicketCode.isSelected)
                    }
                }
                jfxButton("Close".toUpperCase()) {
                    setOnAction { close() }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter = ImportPresenter(
            activeDisplayRepository = activeDisplayRepository,
            timeProvider = timeProvider,
        )
        presenter.onAttach(this)
        presenter.loadWorklogs(
            importWorklogs = emptyList(),
            projectFilter = "",
        )
        renderByImportFilterResult(
            filterResult = importFilters
                .filter(action = IFActionClear)
        )
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
    }

    override fun showWorklogs(worklogViewModels: List<ExportWorklogViewModel>) {
        this.worklogViewModels.clear()
        this.worklogViewModels.addAll(worklogViewModels)
    }

    override fun showProjectFilters(projectFilters: List<String>, filterSelection: String) {
        this.projectFilters.clear()
        this.projectFilters.addAll(projectFilters)
    }

    override fun showImportSuccess() {
        dialogs.showDialogInfo(
            uiComponent = this,
            header = strings.getString("generic_dialog_header_success"),
            content = "Worklogs imported!",
        )
        close()
    }

    override fun showTotal(totalAsString: String) {
        viewTotal.text = "Total: $totalAsString"
    }

    private fun renderByImportFilterResult(
        filterResult: ImportFilterResultState,
    ) {
        l.debug(
            "renderByImportFilterResult(filterResult: {})".withLogInstance(this),
            filterResult,
        )
        // Render checkboxes
        viewCheckNoChanges.isSelected = filterResult.isSelectNoChanges
        viewCheckNoTicketCode.isSelected = filterResult.isSelectNoTickets
        viewCheckTicketCodeFromComment.isSelected = filterResult.isSelectTicketCodeFromComments
        viewCheckTicketCodeAndRemoveFromComment.isSelected = filterResult.isSelectTicketCodeAndRemoveFromComments

        // Render imported worklogs
        val projectFilter = viewProjectFilters.selectionModel.selectedItem
        when (filterResult.action) {
            IFActionClear -> presenter.filterClear(projectFilter)
            IFActionNoTicketCode -> presenter.filterWorklogsNoCode(projectFilter)
            IFActionTicketCodeFromComments -> presenter.filterWorklogsWithCodeFromComment(projectFilter)
            IFActionTicketCodeAndRemoveFromComment -> presenter.filterWorklogsWithCodeAndRemoveFromComment(projectFilter)
        }.javaClass
    }

    companion object {
        private val l = LoggerFactory.getLogger(ImportWidget::class.java)!!
    }

}
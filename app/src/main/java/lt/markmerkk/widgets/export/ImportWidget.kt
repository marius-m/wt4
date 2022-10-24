package lt.markmerkk.widgets.export

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
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
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.checkbox
import tornadofx.combobox
import tornadofx.hbox
import tornadofx.label
import tornadofx.listview
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

class ImportWidget : Fragment(), ImportContract.View {

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

    private lateinit var viewLogs: ListView<ExportWorklogViewModel>
    private lateinit var viewCheckNoTicketCode: CheckBox
    private lateinit var viewCheckTicketCodeFromComment: CheckBox
    private lateinit var viewCheckProjectFilters: CheckBox
    private lateinit var viewProjectFilters: ComboBox<String>
    private lateinit var viewTotal: Label

    private val worklogViewModels = mutableListOf<ExportWorklogViewModel>().asObservable()
    private val projectFilters = mutableListOf<String>().asObservable()
    private val importFilters = ImportFilters(
        defaultProjectFilter = ImportPresenter.PROJECT_FILTER_ALL,
    )

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
                label {
                    text = "Import worklogs from file"
                    isWrapText = true
                }
                viewCheckNoTicketCode = checkbox("No ticket code") {
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
                                .filter(action = IFActionTicketFromComments)
                        )
                    }
                }
                hbox(spacing = 4) {
                    viewCheckProjectFilters = checkbox("") {
                        setOnAction {
                            renderByImportFilterResult(
                                filterResult = importFilters
                                    .filter(action = IFActionTicketProjectFilterDefault)
                            )
                        }
                    }
                    viewProjectFilters = combobox(SimpleStringProperty(""), projectFilters) {
                        setOnAction {
                            val selectItem = (it.source as ComboBox<String>)
                                .selectionModel
                                .selectedItem ?: ""
                            renderByImportFilterResult(
                                filterResult = importFilters
                                    .filter(action = IFActionTicketProjectFilter(filter = selectItem))
                            )
                        }
                    }
                }
                viewLogs = listview(worklogViewModels) {
                    prefHeight = 200.0
                    vgrow = Priority.ALWAYS
                    cellFragment(ExportWorklogItemFragment::class)
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
                                .filter(action = IFActionNoAction)
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
                .filter(action = IFActionNoAction)
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
        viewCheckNoTicketCode.isSelected = filterResult.isSelectNoTickets
        viewCheckTicketCodeFromComment.isSelected = filterResult.isSelectTicketFromComments
        viewCheckProjectFilters.isSelected = filterResult.isSelectTicketFilter
        viewProjectFilters.isDisable = !filterResult.isEnabledTicketFilter

        // Render combobox selection
        when (filterResult.action) {
            IFActionNoAction,
            IFActionTicketProjectFilterDefault -> {
                val selectIndex = projectFilters.indexOf(filterResult.ticketFilter)
                viewProjectFilters.selectionModel.clearAndSelect(selectIndex)
            }
            else -> {
                // No action on viewProjectFilter selection, as it would trigger filter change again
            }
        }

        // Render imported worklogs
        when (filterResult.action) {
            IFActionNoAction,
            IFActionNoTicketCode,
            is IFActionTicketProjectFilter,
            IFActionTicketProjectFilterDefault -> presenter.filterWorklogsByProject(filterResult.ticketFilter)
            IFActionTicketFromComments -> presenter.filterWorklogsWithCodeFromComment()
        }
    }

    companion object {
        private val l = LoggerFactory.getLogger(ImportWidget::class.java)!!
    }

}
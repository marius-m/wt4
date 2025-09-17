package lt.markmerkk.widgets.export

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Strings
import lt.markmerkk.Styles
import lt.markmerkk.WorklogStorage
import lt.markmerkk.export.ExportContract
import lt.markmerkk.export.ExportPresenter
import lt.markmerkk.export.WorklogExporter
import lt.markmerkk.export.entities.ExportWorklogViewModel
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.widgets.dialogs.Dialogs
import lt.markmerkk.widgets.export.tableview.ImportTableCell
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.column
import tornadofx.combobox
import tornadofx.hbox
import tornadofx.label
import tornadofx.listview
import tornadofx.readonlyColumn
import tornadofx.tableview
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject
import lt.markmerkk.ui_2.BaseFragment

class ExportWidget : BaseFragment(), ExportContract.View {

    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var worklogExporter: WorklogExporter
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository
    @Inject lateinit var dialogs: Dialogs
    @Inject lateinit var strings: Strings

    init {
        Main.component().inject(this)
    }

    private lateinit var viewLogs: TableView<ExportWorklogViewModel>
    private lateinit var viewProjectFilters: ComboBox<String>
    private lateinit var viewTotal: Label

    private lateinit var presenter: ExportContract.Presenter

    private val worklogViewModels = mutableListOf<ExportWorklogViewModel>().asObservable()
    private val projectFilters = mutableListOf<String>().asObservable()

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Export worklogs") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            vbox(spacing = 4.0) {
                label {
                    val startDate = LogFormatters.formatDate.print(activeDisplayRepository.displayDateRange.start)
                    val endDate = LogFormatters.formatDate.print(activeDisplayRepository.displayDateRange.endAsNextDay)
                    text = "Worklogs from $startDate to $endDate"
                    isWrapText = true
                }
                label {
                    text = "Select worklogs to export to file"
                    isWrapText = true
                }
                viewProjectFilters = combobox(SimpleStringProperty(""), projectFilters) {
                    setOnAction {
                        val selectItem = (it.source as ComboBox<String>)
                                .selectionModel
                                .selectedItem ?: ""
                        presenter.loadWorklogs(projectFilter = selectItem)
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
                jfxButton("Sample".toUpperCase()) {
                    action {
                        presenter.sampleExport(worklogViewModels)
                    }
                }
                jfxButton("Export".toUpperCase()) {
                    action {
                        presenter.exportWorklogs(worklogViewModels)
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
        presenter = ExportPresenter(
            worklogStorage,
            worklogExporter,
            activeDisplayRepository
        )
        presenter.onAttach(this)
        presenter.loadWorklogs(projectFilter = presenter.defaultProjectFilter)
        presenter.loadProjectFilters()
    }

    override fun onUndock() {
        presenter.onDetach()
        super.onUndock()
    }

    override fun showWorklogsForExport(worklogViewModels: List<ExportWorklogViewModel>) {
        this.worklogViewModels.clear()
        this.worklogViewModels.addAll(worklogViewModels)
    }

    override fun showProjectFilters(projectFilters: List<String>, filterSelection: String) {
        this.projectFilters.clear()
        this.projectFilters.addAll(projectFilters)
        this.viewProjectFilters.selectionModel.select(filterSelection)
    }

    override fun showExportSample(sampleAsString: String) {
        resultDispatcher.publish(ExportSampleWidget.RESULT_DISPATCH_KEY_SAMPLE, sampleAsString)
        find<ExportSampleWidget>().openModal(
                stageStyle = StageStyle.DECORATED,
                modality = Modality.APPLICATION_MODAL,
                block = false,
                resizable = true
        )
    }

    override fun showExportSuccess() {
        dialogs.showDialogInfo(
            uiComponent = this,
            header = strings.getString("generic_dialog_header_success"),
            content = "Worklogs exported!",
        )
    }

    override fun showExportFailure() {
        dialogs.showDialogInfo(
            uiComponent = this,
            header = strings.getString("generic_dialog_header_error"),
            content = "Error saving worklogs",
        )
    }

    override fun showTotal(totalAsString: String) {
        viewTotal.text = "Total: $totalAsString"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExportWidget::class.java)!!
    }

}
package lt.markmerkk.widgets.export

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import lt.markmerkk.*
import lt.markmerkk.export.ImportContract
import lt.markmerkk.export.ImportPresenter
import lt.markmerkk.export.WorklogExporter
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.export.entities.ExportWorklogViewModel
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class ImportWidget : Fragment(), ImportContract.View {

    @Inject lateinit var dayProvider: DayProvider
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var worklogExporter: WorklogExporter
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository

    init {
        Main.component().inject(this)
    }

    private lateinit var presenter: ImportContract.Presenter

    private lateinit var viewLogs: ListView<ExportWorklogViewModel>
    private lateinit var viewSkipTicketCode: CheckBox
    private lateinit var viewProjectFilters: ComboBox<String>
    private lateinit var viewTotal: Label

    private val worklogViewModels = mutableListOf<ExportWorklogViewModel>().asObservable()
    private val projectFilters = mutableListOf<String>().asObservable()

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
                viewSkipTicketCode = checkbox("Skip ticket code when importing") { }
                viewProjectFilters = combobox(SimpleStringProperty(""), projectFilters) {
                    setOnAction {
                        val selectItem = (it.source as ComboBox<String>)
                                .selectionModel
                                .selectedItem ?: ""
                        presenter.filterWorklogs(projectFilter = selectItem)
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
                        presenter.loadProjectFilters(importWorklogs)
                    }
                }
                jfxButton("Import".toUpperCase()) {
                    setOnAction {
                        presenter.import(worklogViewModels, viewSkipTicketCode.isSelected)
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
            activeDisplayRepository = activeDisplayRepository
        )
        presenter.onAttach(this)
        presenter.filterWorklogs(projectFilter = presenter.defaultProjectFilter)
        presenter.loadProjectFilters(worklogs = emptyList())
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
        this.viewProjectFilters.selectionModel.select(filterSelection)
    }

    override fun showImportSuccess() {
        information(
                header = "Success",
                content = "Worklogs imported!"
        )
        close()
    }

    override fun showTotal(totalAsString: String) {
        viewTotal.text = "Total: $totalAsString"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ImportWidget::class.java)!!
    }

}
package lt.markmerkk.widgets.export

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.export.ExportContract
import lt.markmerkk.export.ExportPresenter
import lt.markmerkk.export.WorklogExporter
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.export.entities.ExportWorklogViewModel
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class ExportWidget : Fragment(), ExportContract.View {

    @Inject lateinit var dayProvider: DayProvider
    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var worklogExporter: WorklogExporter

    init {
        Main.component().inject(this)
    }

    private lateinit var viewLogs: ListView<ExportWorklogViewModel>
    private lateinit var viewProjectFilters: ComboBox<String>

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
                    val startDate = dayProvider.startAsDate().toString(LogFormatters.DATE_SHORT_FORMAT)
                    val endDate = dayProvider.endAsDate().toString(LogFormatters.DATE_SHORT_FORMAT)
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
                viewLogs = listview(worklogViewModels) {
                    prefHeight = 200.0
                    vgrow = Priority.ALWAYS
                    cellFragment(ExportWorklogItemFragment::class)
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
                dayProvider,
                worklogExporter
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
        information(
                header = "Success",
                content = "Worklogs exported!"
        )
    }

    override fun showExportFailure() {
        error(
                header = "Error",
                content = "Error saving worklogs"
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExportWidget::class.java)!!
    }

}
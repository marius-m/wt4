package lt.markmerkk.widgets.export

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import lt.markmerkk.*
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.TimedCommentStamper
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class ExportWidget : Fragment() {

    @Inject lateinit var dayProvider: DayProvider
    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var resultDispatcher: ResultDispatcher

    init {
        Main.component().inject(this)
    }

    private lateinit var viewLogs: ListView<ExportWorklogViewModel>

    private val exportWorklogViewModels = mutableListOf<ExportWorklogViewModel>()
            .asObservable()

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
                    val endDate = dayProvider.startAsDate().toString(LogFormatters.DATE_SHORT_FORMAT)
                    text = "Worklogs from $startDate to $endDate"
                    isWrapText = true
                }
                label {
                    text = "Select worklogs to export to file"
                    isWrapText = true
                }
                viewLogs = listview(exportWorklogViewModels) {
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
                        val logsAsString = exportWorklogViewModels
                                .filter { it.selectedProperty.get() }
                                .map { it.log }
                                .map { LogFormatters.formatLogBasic(it) }
                                .joinToString("\n")
                        resultDispatcher.publish(ExportSampleWidget.RESULT_DISPATCH_KEY_SAMPLE, logsAsString)
                        find<ExportSampleWidget>().openModal(
                                stageStyle = StageStyle.DECORATED,
                                modality = Modality.APPLICATION_MODAL,
                                block = false,
                                resizable = true
                        )
                    }
                }
                jfxButton("Export".toUpperCase()) {
                    action {
                        val logsForExport = exportWorklogViewModels
                                .filter { it.selectedProperty.get() }
                                .map { it.log }
                        logger.debug("Exporting $logsForExport")
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
        val worklogs = worklogStorage.loadWorklogsSync(
                from = dayProvider.startAsDate(),
                to = dayProvider.endAsDate()
        ).map {
            ExportWorklogViewModel(it)
        }
        exportWorklogViewModels.clear()
        exportWorklogViewModels.addAll(worklogs)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExportWidget::class.java)!!
    }

}
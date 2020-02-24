package lt.markmerkk.widgets.export

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import lt.markmerkk.*
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.widgets.export.entities.ExportWorklogViewModel
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class ImportWidget : Fragment() {

    @Inject lateinit var dayProvider: DayProvider
    @Inject lateinit var worklogStorage: WorklogStorage
    @Inject lateinit var resultDispatcher: ResultDispatcher
    @Inject lateinit var worklogExporter: WorklogExporter
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var timeProvider: TimeProvider

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
                jfxButton("Load".toUpperCase()) {
                    action {
                        val importWorklogs = worklogExporter.importFromFile()
                        val worklogViewModels = importWorklogs
                                .sortedBy { it.time.start }
                                .map { ExportWorklogViewModel(it, includeDate = true) }
                        exportWorklogViewModels.clear()
                        exportWorklogViewModels.addAll(worklogViewModels)
                    }
                }
                jfxButton("Import".toUpperCase()) {
                    action {
                        logger.debug("Importing worklogs")
                        exportWorklogViewModels
                                .filter { it.selectedProperty.get() }
                                .map { it.log }
                                .forEach { logStorage.insert(it.toLegacyLog(timeProvider)) }
                        close()
                    }
                }
                jfxButton("Close".toUpperCase()) {
                    setOnAction { close() }
                }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ImportWidget::class.java)!!
    }

}
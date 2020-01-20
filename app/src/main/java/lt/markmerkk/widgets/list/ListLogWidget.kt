package lt.markmerkk.widgets.list

import com.jfoenix.svg.SVGGlyph
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.TableView
import lt.markmerkk.*
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SyncStatus
import lt.markmerkk.events.EventEditLog
import lt.markmerkk.ui_2.views.ContextMenuEditLog
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.widgets.MainContainerNavigator
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import javax.inject.Inject

class ListLogWidget: Fragment(), IDataListener<SimpleLog> {

    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus

    init {
        Main.component().inject(this)
    }

    private lateinit var viewTable: TableView<LogViewModel>

    private val mainContainerNavigator = MainContainerNavigator(
            logStorage,
            eventBus,
            this
    )
    private val contextMenuEditLog: ContextMenuEditLog = ContextMenuEditLog(
            strings,
            graphics,
            logStorage,
            eventBus,
            listOf(
                    LogEditType.NEW,
                    LogEditType.UPDATE,
                    LogEditType.CLONE,
                    LogEditType.DELETE,
                    LogEditType.SPLIT,
                    LogEditType.WEBLINK,
                    LogEditType.BROWSER
            )
    )
    private val logs = mutableListOf<LogViewModel>()
            .observable()

    override val root: Parent = tableview(logs) {
        viewTable = this
        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        JavaFxObservable.valuesOf(selectionModel.selectedItemProperty())
                .subscribe { selectItem ->
                    val selectItemIds = listOf(selectItem)
                            .filter { it != null }
                            .filter { selectItem.editable }
                            .map { selectItem.id }
                    contextMenuEditLog.bindLogs(selectItemIds)
                }
        setOnMouseClicked { mouseEvent ->
            val selectLogId = viewTable.selectionModel.selectedItems.firstOrNull()?.id
            val selectLog = logStorage.findByIdOrNull(selectLogId ?: -1)
            if (mouseEvent.clickCount >= 2 && selectLog != null) {
                eventBus.post(EventEditLog(LogEditType.UPDATE, listOf(selectLog)))
            }
        }
        contextMenu = contextMenuEditLog.root
        column("S", LogViewModel::color) {
            minWidth = 32.0
            maxWidth = 32.0
            cellFragment(ListLogStatusIndicatorCell::class) // probably displays incorrect values when re-rendering
        }
        readonlyColumn("Ticket", LogViewModel::ticketCode) {
            minWidth = 100.0
            maxWidth = 100.0
        }
        readonlyColumn("Start", LogViewModel::start) {
            minWidth = 60.0
            maxWidth = 60.0
        }
        readonlyColumn("End", LogViewModel::end) {
            minWidth = 60.0
            maxWidth = 60.0
        }
        readonlyColumn("Duration", LogViewModel::duration) {
            minWidth = 60.0
            maxWidth = 60.0
        }
        readonlyColumn("Comment", LogViewModel::comment)
    }

    override fun onDock() {
        super.onDock()
        onDataChange(logStorage.data)
        logStorage.register(this)
        mainContainerNavigator.onAttach()
    }

    override fun onUndock() {
        mainContainerNavigator.onDetach()
        logStorage.unregister(this)
        super.onUndock()
    }

    // todo should be moved to presenter side
    override fun onDataChange(data: List<SimpleLog>) {
        val totalViewModel = LogViewModel(
                id = -1,
                editable = false,
                syncStatusColor = SyncStatus.INVALID.toColor().toString(),
                ticketCode = "TOTAL",
                start = "",
                end = "",
                duration = LogUtils.formatShortDurationMillis(logStorage.total().toLong()),
                comment = ""
        )
        val logViewModels = logStorage.data
                .map {
                    LogViewModel(
                            id = it._id,
                            editable = true,
                            syncStatusColor = SyncStatus.exposeStatus(it).toColor().toString(),
                            ticketCode = it.task,
                            start = LogFormatters.shortFormat.print(it.start),
                            end = LogFormatters.shortFormat.print(it.end),
                            duration = LogUtils.formatShortDurationMillis(it.duration),
                            comment = it.comment
                    )
                }
                .sortedBy { it.start }
                .plus(totalViewModel)
        logs.clear()
        logs.addAll(logViewModels)
    }

    data class LogViewModel(
            val id: Long,
            val editable: Boolean,
            val syncStatusColor: String,
            val ticketCode: String,
            val start: String,
            val end: String,
            val duration: String,
            val comment: String
    ) {
        val colorProperty = SimpleStringProperty(syncStatusColor)
        var color by colorProperty
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ListLogWidget::class.java)!!
    }

}
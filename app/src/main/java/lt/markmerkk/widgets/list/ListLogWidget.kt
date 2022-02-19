package lt.markmerkk.widgets.list

import com.google.common.eventbus.Subscribe
import com.jfoenix.svg.SVGGlyph
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.TableView
import lt.markmerkk.ActiveDisplayRepository
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.Strings
import lt.markmerkk.WTEventBus
import lt.markmerkk.WorklogStorage
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SyncStatus
import lt.markmerkk.events.EventActiveDisplayDataChange
import lt.markmerkk.events.EventEditLog
import lt.markmerkk.ui_2.views.ContextMenuEditLog
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.widgets.MainContainerNavigator
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.Fragment
import tornadofx.asObservable
import tornadofx.column
import tornadofx.getValue
import tornadofx.observable
import tornadofx.readonlyColumn
import tornadofx.setValue
import tornadofx.tableview
import javax.inject.Inject

class ListLogWidget: Fragment() {

    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository
    @Inject lateinit var worklogStorage: WorklogStorage

    init {
        Main.component().inject(this)
    }

    private lateinit var viewTable: TableView<LogViewModel>

    private val mainContainerNavigator = MainContainerNavigator(
        eventBus = eventBus,
        uiComponent = this,
        activeDisplayRepository = activeDisplayRepository
    )
    private val contextMenuEditLog: ContextMenuEditLog = ContextMenuEditLog(
        strings,
        graphics,
        eventBus,
        worklogStorage,
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
            .asObservable()

    override val root: Parent = tableview(logs) {
        viewTable = this
        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        JavaFxObservable.valuesOf(selectionModel.selectedItemProperty())
                .subscribe({ selectItem ->
                    val selectItemIds = listOf(selectItem)
                            .filter { it != null }
                            .filter { selectItem.editable }
                            .map { selectItem.id }
                    contextMenuEditLog.bindLogs(selectItemIds)
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        setOnMouseClicked { mouseEvent ->
            val selectLogId = viewTable.selectionModel.selectedItems.firstOrNull()?.id
            val selectLog = worklogStorage.findById(selectLogId ?: -1)
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
        mainContainerNavigator.onAttach()
        eventBus.register(this)
        reloadLogs(activeDisplayRepository.displayLogs)
    }

    override fun onUndock() {
        eventBus.unregister(this)
        mainContainerNavigator.onDetach()
        super.onUndock()
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

    @Subscribe
    fun onLogStorageDataChange(event: EventActiveDisplayDataChange) {
        reloadLogs(event.data)
    }

    private fun reloadLogs(logs: List<Log>) {
        val totalViewModel = LogViewModel(
            id = -1,
            editable = false,
            syncStatusColor = SyncStatus.INVALID.toColor().toString(),
            ticketCode = "TOTAL",
            start = "",
            end = "",
            duration = LogUtils.formatShortDurationMillis(activeDisplayRepository.totalInMillis()),
            comment = ""
        )
        val logViewModels = activeDisplayRepository.displayLogs
            .map {
                LogViewModel(
                    id = it.id,
                    editable = true,
                    syncStatusColor = SyncStatus.exposeStatus(it).toColor().toString(),
                    ticketCode = it.code.code,
                    start = LogFormatters.shortFormat.print(it.time.start),
                    end = LogFormatters.shortFormat.print(it.time.end),
                    duration = LogUtils.formatShortDurationMillis(it.time.duration.millis),
                    comment = it.comment
                )
            }
            .sortedBy { it.start }
            .plus(totalViewModel)
        this.logs.clear()
        this.logs.addAll(logViewModels)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ListLogWidget::class.java)!!
    }

}
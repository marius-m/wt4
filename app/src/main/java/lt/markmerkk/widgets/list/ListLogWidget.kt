package lt.markmerkk.widgets.list

import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.TableView
import lt.markmerkk.IDataListener
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SyncStatus
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.utils.LogUtils
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class ListLogWidget: View(), IDataListener<SimpleLog> {

    @Inject lateinit var logStorage: LogStorage

    init {
        Main.component().inject(this)
    }

    private val logs = mutableListOf<LogViewModel>()
            .observable()

    override val root: Parent = tableview(logs) {
        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        column("S", LogViewModel::color) {
            minWidth = 32.0
            maxWidth = 32.0
            cellFragment(ListLogStatusIndicatorCell::class)
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
    }

    override fun onUndock() {
        logStorage.unregister(this)
        super.onUndock()
    }

    override fun onDataChange(data: List<SimpleLog>) {
        val totalViewModel = LogViewModel(
                editable = false,
                syncStatusColor = SyncStatus.INVALID.toColor().toString(),
                ticketCode = "TOTAL",
                start = "",
                end = "",
                duration = LogUtils.formatShortDuration(logStorage.total().toLong()),
                comment = ""
        )
        val logViewModels = logStorage.data
                .map {
                    LogViewModel(
                            editable = true,
                            syncStatusColor = SyncStatus.exposeStatus(it).toColor().toString(),
                            ticketCode = it.task,
                            start = LogFormatters.shortFormat.print(it.start),
                            end = LogFormatters.shortFormat.print(it.end),
                            duration = LogUtils.formatShortDuration(it.duration),
                            comment = it.comment
                    )
                }
                .sortedBy { it.start }
                .plus(totalViewModel)
        logs.clear()
        logs.addAll(logViewModels)
    }

    data class LogViewModel(
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
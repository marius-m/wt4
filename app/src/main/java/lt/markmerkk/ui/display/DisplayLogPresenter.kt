package lt.markmerkk.ui.display

import com.jfoenix.svg.SVGGlyph
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui_2.views.ContextMenuEditLog
import lt.markmerkk.utils.LogDisplayController
import lt.markmerkk.utils.TableDisplayController
import lt.markmerkk.utils.tracker.ITracker
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Represents the presenter to display the log list
 */
class DisplayLogPresenter : Initializable, IDataListener<SimpleLog> {

    @Inject lateinit var storage: LogStorage
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    @FXML lateinit var tableView: TableView<SimpleLog>

    private val logs: ObservableList<SimpleLog> = FXCollections.observableArrayList()
    private lateinit var contextMenuEditLog: ContextMenuEditLog
    private lateinit var tableDisplayController: TableDisplayController

    override fun initialize(location: URL, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        contextMenuEditLog = ContextMenuEditLog(strings, graphics, storage, eventBus)
        logs.addAll(storage.data)
        tableView.tooltip = Tooltip(Translation.getInstance().getString("daylog_tooltip_title"))
        tracker.sendView(GAStatics.VIEW_DAY_SIMPLE)
        tableDisplayController = LogDisplayController(tableView, logs, contextMenuEditLog, eventBus)

        storage.register(this)
        tableDisplayController.onAttach()
    }

    @PreDestroy
    fun destroy() {
        tableDisplayController.onDetach()
        storage.unregister(this)
    }

    override fun onDataChange(data: List<SimpleLog>) {
        logs.clear()
        logs.addAll(data)
        tableView.sort()
        }

}

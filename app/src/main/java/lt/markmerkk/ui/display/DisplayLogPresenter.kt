package lt.markmerkk.ui.display

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.utils.LogDisplayController
import lt.markmerkk.utils.LogDisplayControllerSimple
import lt.markmerkk.utils.TableDisplayController
import lt.markmerkk.utils.tracker.ITracker
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
class DisplayLogPresenter : Initializable, IDataListener<SimpleLog> {
    @Inject
    lateinit var storage: LogStorage
    @Inject
    lateinit var tracker: ITracker

    @FXML
    lateinit var tableView: TableView<SimpleLog>

    private val logs: ObservableList<SimpleLog> = FXCollections.observableArrayList()
    private var updateListener: UpdateListener? = null

    override fun initialize(location: URL, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)
        logs.addAll(storage.data)
        tableView.tooltip = Tooltip(Translation.getInstance().getString("daylog_tooltip_title"))
        storage.register(this)
    }

    @PreDestroy
    fun destroy() {
        storage.unregister(this)
    }

    fun setUpdateListener(updateListener: UpdateListener) {
        this.updateListener = updateListener
    }

    /**
     * Initializes table view with option to be simplified (drops unnecessary table columns)
     */
    fun initTableView(viewSimplified: Boolean) {
        if (viewSimplified) {
            tracker.sendView(GAStatics.VIEW_DAY_SIMPLE)
        } else {
            tracker.sendView(GAStatics.VIEW_DAY)
        }
        if (viewSimplified) {
            LogDisplayControllerSimple(tableView, logs, tableDisplayControllerListener)
            return
        }
        LogDisplayController(tableView, logs, tableDisplayControllerListener)
    }

    override fun onDataChange(data: List<SimpleLog>) {
        logs.clear()
        logs.addAll(data)
        tableView.sort()
    }

    //region Listeners

    private val tableDisplayControllerListener: TableDisplayController.Listener<SimpleLog> = object : TableDisplayController.Listener<SimpleLog> {
        override fun onUpdate(updateableObject: SimpleLog) {
            updateListener?.onUpdate(updateableObject)
        }

        override fun onDelete(deleteableObject: SimpleLog) {
            updateListener?.onDelete(deleteableObject)
        }

        override fun onClone(cloneableObject: SimpleLog) {
            updateListener?.onClone(cloneableObject)
        }
    }

    //endregion
}

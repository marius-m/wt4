package lt.markmerkk.ui.display

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import lt.markmerkk.IDataListener
import lt.markmerkk.IDataStorage
import lt.markmerkk.Main
import lt.markmerkk.Translation
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.listeners.IPresenter
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.utils.LogDisplayController
import lt.markmerkk.utils.TableDisplayController
import lt.markmerkk.utils.tracker.SimpleTracker

import javax.annotation.PreDestroy
import javax.inject.Inject
import java.net.URL
import java.util.ResourceBundle

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
class DisplayLogPresenter : Initializable, IPresenter, IDataListener<SimpleLog> {
    @Inject
    lateinit var storage: LogStorage

    @FXML
    lateinit var tableView: TableView<SimpleLog>

    private val logs: ObservableList<SimpleLog> = FXCollections.observableArrayList()
    private var updateListener: UpdateListener? = null

    override fun initialize(location: URL, resources: ResourceBundle?) {
        Main.getComponent().presenterComponent().inject(this)
        SimpleTracker.getInstance().tracker.sendView(SimpleTracker.VIEW_DAY)
        logs.addAll(storage.data)

        tableView?.tooltip = Tooltip(Translation.getInstance().getString("daylog_tooltip_title"))
        LogDisplayController(tableView, logs, object : TableDisplayController.Listener<SimpleLog> {
            override fun onUpdate(updateableObject: SimpleLog) {
                updateListener?.onUpdate(updateableObject)
            }

            override fun onDelete(deleteableObject: SimpleLog) {
                updateListener?.onDelete(deleteableObject)
            }

            override fun onClone(cloneableObject: SimpleLog) {
                updateListener?.onClone(cloneableObject)
            }
        })
        storage.register(this)
    }

    @PreDestroy
    fun destroy() {
        storage.unregister(this)
    }

    fun setUpdateListener(updateListener: UpdateListener) {
        this.updateListener = updateListener
    }

    override fun onDataChange(data: List<SimpleLog>) {
        logs.clear()
        logs.addAll(data)
    }

}

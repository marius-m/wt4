package lt.markmerkk.ui.day

import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.calendarfx.view.DateControl
import com.calendarfx.view.DetailedDayView
import com.jfoenix.svg.SVGGlyph
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.util.Callback
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.utils.CalendarFxLogLoader
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class DayPresenter : Initializable {
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    @FXML private lateinit var jfxDayContainer: StackPane
    @FXML private lateinit var jfxDayView: DetailedDayView

    lateinit var updateListener: UpdateListener
    private lateinit var logLoader: CalendarFxLogLoader

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        tracker.sendView(GAStatics.VIEW_CALENDAR_DAY)
        storage.register(storageListener)

        val calendar = com.calendarfx.model.Calendar()
        calendar.setStyle(com.calendarfx.model.Calendar.Style.STYLE1)
        calendar.isReadOnly = true
        val calendarSource = CalendarSource()
        calendarSource.calendars.addAll(calendar)
        jfxDayView.calendarSources.add(calendarSource)
        jfxDayView.isShowAllDayView = false
        jfxDayView.entryDetailsCallback = object : Callback<DateControl.EntryDetailsParameter, Boolean> {
            override fun call(param: DateControl.EntryDetailsParameter): Boolean {
                if (param.inputEvent.eventType != MouseEvent.MOUSE_CLICKED) {
                    return true
                }
                if ((param.inputEvent as MouseEvent).clickCount < 2) {
                    return true
                }
                updateListener.onUpdate(param.entry.userObject as SimpleLog)
                return true
            }
        }
        jfxDayView.entryContextMenuCallback = object : Callback<DateControl.EntryContextMenuParameter, ContextMenu> {
            override fun call(param: DateControl.EntryContextMenuParameter): ContextMenu {
                val contextMenu = ContextMenu()
                val updateItem = MenuItem(strings.getString("general_update"),
                        ImageView(Image(javaClass.getResource("/update_2.png").toString())))
                updateItem.onAction = EventHandler<ActionEvent> {
                    updateListener.onUpdate(param.entry.userObject as SimpleLog)
                }
                val deleteItem = MenuItem(strings.getString("general_delete"),
                        ImageView(Image(javaClass.getResource("/delete_2.png").toString())))
                deleteItem.onAction = EventHandler<ActionEvent> {
                    updateListener.onDelete(param.entry.userObject as SimpleLog)
                }
                val cloneItem = MenuItem(strings.getString("general_clone"),
                        ImageView(Image(javaClass.getResource("/clone_2.png").toString())))
                cloneItem.onAction = EventHandler<ActionEvent> {
                    updateListener.onClone(param.entry.userObject as SimpleLog)
                }
                contextMenu.items.addAll(updateItem, deleteItem, cloneItem)
                return contextMenu
            }
        }

        logLoader = CalendarFxLogLoader(
                object : CalendarFxLogLoader.View {
                    override fun onCalendarEntries(calendarEntries: List<Entry<SimpleLog>>) {
                        calendar.startBatchUpdates()
                        calendar.clear()
                        calendar.addEntries(calendarEntries)
                        calendar.stopBatchUpdates()
                    }
                },
                Schedulers.io(),
                JavaFxScheduler.getInstance()
        )
        logLoader.onAttach()
        logLoader.load(storage.data)
    }

    @PreDestroy
    fun destroy() {
        logLoader.onDetach()
        storage.unregister(storageListener)
    }

    //region Listeners

    private val storageListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            logLoader.load(data)
        }
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(DayPresenter::class.java)!!
    }

}

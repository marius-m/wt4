package lt.markmerkk.ui.day

import com.calendarfx.model.Calendar
import com.calendarfx.model.CalendarEvent
import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.calendarfx.view.DateControl
import com.jfoenix.svg.SVGGlyph
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.Callback
import lt.markmerkk.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.ui.interfaces.UpdateListener
import lt.markmerkk.utils.CalendarFxLogLoader
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class CalendarPresenter : Initializable {
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>

    @FXML private lateinit var jfxContainer: StackPane
    @FXML private lateinit var jfxCalendarView: DateControl

    lateinit var updateListener: UpdateListener

    private val calendar = Calendar()
    private val calendarSource = CalendarSource().apply {
        calendars.addAll(calendar)
    }
    private lateinit var logLoader: CalendarFxLogLoader

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component!!.presenterComponent().inject(this)

        tracker.sendView(GAStatics.VIEW_CALENDAR_DAY)
        storage.register(storageListener)

        calendar.setStyle(com.calendarfx.model.Calendar.Style.STYLE1)
        calendar.isReadOnly = false
        calendar.addEventHandler(object : EventHandler<CalendarEvent> {
            override fun handle(event: CalendarEvent) {
                if (event.eventType == CalendarEvent.ENTRY_INTERVAL_CHANGED) {
                    val calendarEntryStart = event.entry.startMillis
                    val calendarEntryEnd = event.entry.endMillis
                    val oldLog = event.entry.userObject as SimpleLog
                    storage.delete(oldLog)
                    storage.insert(
                            SimpleLogBuilder(oldLog)
                                    .setStart(calendarEntryStart)
                                    .setEnd(calendarEntryEnd)
                                    .build()
                    )
                }
            }
        })

        jfxCalendarView.calendarSources.add(calendarSource)
//        jfxDayView.isShowAllDayView = false
//        jfxDayView.isShowAgendaView = false
        jfxCalendarView.entryDetailsCallback = object : Callback<DateControl.EntryDetailsParameter, Boolean> {
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
        jfxCalendarView.entryContextMenuCallback = object : Callback<DateControl.EntryContextMenuParameter, ContextMenu> {
            override fun call(param: DateControl.EntryContextMenuParameter): ContextMenu {
                val contextMenu = ContextMenu()
                val updateItem = MenuItem(
                        strings.getString("general_update"),
                        graphics.glyph("update", Color.BLACK, 16.0, 16.0)
                )
                val deleteItem = MenuItem(
                        strings.getString("general_delete"),
                        graphics.glyph("delete", Color.BLACK, 12.0, 16.0)
                )
                val cloneItem = MenuItem(
                        strings.getString("general_clone"),
                        graphics.glyph("clone", Color.BLACK, 16.0, 12.0)
                )
                contextMenu.items.addAll(updateItem, deleteItem, cloneItem)
                contextMenu.onAction = object : EventHandler<ActionEvent> {
                    override fun handle(event: ActionEvent) {
                        when (event.target) {
                            updateItem -> updateListener.onUpdate(param.entry.userObject as SimpleLog)
                            deleteItem -> updateListener.onDelete(param.entry.userObject as SimpleLog)
                            cloneItem -> updateListener.onClone(param.entry.userObject as SimpleLog)
                        }
                    }
                }
                return contextMenu
            }
        }
        jfxCalendarView.entryFactory = object : Callback<DateControl.CreateEntryParameter, Entry<*>> {
            override fun call(param: DateControl.CreateEntryParameter): Entry<SimpleLog>? {
                val startMillis = param.zonedDateTime.toInstant().toEpochMilli()
                val endMillis = param.zonedDateTime.plusHours(1).toInstant().toEpochMilli()
                val simpleLog = SimpleLogBuilder()
                        .setStart(startMillis)
                        .setEnd(endMillis)
                        .build()
                storage.insert(simpleLog)
                return null
            }
        }
        jfxCalendarView.entryEditPolicy = object : Callback<DateControl.EntryEditParameter, Boolean> {
            override fun call(param: DateControl.EntryEditParameter): Boolean {
                val editableLog = param.entry.userObject as SimpleLog
                if (editableLog.canEdit()
                        && (param.editOperation == DateControl.EditOperation.CHANGE_START
                        || param.editOperation == DateControl.EditOperation.CHANGE_END)) {
                    return true
                }
                return false
            }
        }
        logLoader = CalendarFxLogLoader(
                object : CalendarFxLogLoader.View {
                    override fun onCalendarNoEntries() {
                        calendar.clear()
                    }

                    override fun onCalendarEntries(calendarEntries: List<Entry<SimpleLog>>) {
                        val targetDate = storage.targetDate.toLocalDate() // todo: Provide shown date
                        jfxCalendarView.date = LocalDate.of(
                                targetDate.year,
                                targetDate.monthOfYear,
                                targetDate.dayOfMonth
                        )
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
        val logger = LoggerFactory.getLogger(CalendarPresenter::class.java)!!
    }

}

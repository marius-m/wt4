package lt.markmerkk.ui.day

import com.calendarfx.model.Calendar
import com.calendarfx.model.CalendarEvent
import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.calendarfx.view.DateControl
import com.calendarfx.view.DetailedDayView
import com.calendarfx.view.DetailedWeekView
import com.google.common.eventbus.EventBus
import com.jfoenix.svg.SVGGlyph
import javafx.collections.SetChangeListener
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
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.events.EventEditLog
import lt.markmerkk.events.EventLogSelection
import lt.markmerkk.ui.ExternalSourceNode
import lt.markmerkk.ui_2.bridges.UIEButtonCalendarQuickEdit
import lt.markmerkk.ui_2.views.ContextMenuEditLog
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerPresenter
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContainerWidget
import lt.markmerkk.ui_2.views.calendar_edit.QuickEditContract
import lt.markmerkk.utils.CalendarFxLogLoader
import lt.markmerkk.utils.CalendarFxUpdater
import lt.markmerkk.utils.CalendarMenuItemProvider
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.validators.LogChangeValidator
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class CalendarPresenter : Initializable {
    @Inject lateinit var storage: LogStorage
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logChangeValidator: LogChangeValidator
    @Inject lateinit var eventBus: EventBus

    @FXML private lateinit var jfxContainer: StackPane
    @FXML private lateinit var jfxCalendarView: DateControl

    private lateinit var contextMenu: ContextMenuEditLog

    private val calendarChangeEventHandler: EventHandler<CalendarEvent> = object : EventHandler<CalendarEvent> {
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
    }
    private val calendarInSync = Calendar().apply {
        setStyle(Calendar.Style.STYLE1)
        isReadOnly = true
        addEventHandler(calendarChangeEventHandler)
    }
    private val calendarWaitingForSync = Calendar().apply {
        setStyle(Calendar.Style.STYLE3)
        isReadOnly = false
        addEventHandler(calendarChangeEventHandler)
    }
    private val calendarError = Calendar().apply {
        setStyle(Calendar.Style.STYLE5)
        isReadOnly = false
        addEventHandler(calendarChangeEventHandler)
    }
    private val calendarSource = CalendarSource().apply {
        calendars.addAll(calendarInSync, calendarWaitingForSync, calendarError)
    }

    private lateinit var logLoader: CalendarFxLogLoader
    private lateinit var calendarUpdater: CalendarFxUpdater

    private var selectedId: Long = Const.NO_ID

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.component().presenterComponent().inject(this)

        contextMenu = ContextMenuEditLog(strings, graphics, storage, eventBus)
        tracker.sendView(GAStatics.VIEW_CALENDAR_DAY)
        storage.register(storageListener)

        jfxCalendarView.calendarSources.add(calendarSource)
        if (jfxCalendarView is com.calendarfx.view.DetailedDayView) {
            val jfxDayView = jfxCalendarView as com.calendarfx.view.DetailedDayView
            jfxDayView.isShowAllDayView = false
            jfxDayView.isShowAgendaView = false
        }
        if (jfxCalendarView is com.calendarfx.view.DetailedWeekView) {
            val jfxDayView = jfxCalendarView as com.calendarfx.view.DetailedWeekView
            jfxDayView.isShowAllDayView = false
        }
        jfxCalendarView.entryDetailsCallback = object : Callback<DateControl.EntryDetailsParameter, Boolean> {
            override fun call(param: DateControl.EntryDetailsParameter): Boolean {
                if (param.inputEvent.eventType != MouseEvent.MOUSE_CLICKED) {
                    return true
                }
                if ((param.inputEvent as MouseEvent).clickCount < 2) {
                    return true
                }
                val simpleLog = param.entry.userObject as SimpleLog
                eventBus.post(EventEditLog(LogEditType.UPDATE, listOf(simpleLog)))
                return true
            }
        }
        jfxCalendarView.entryContextMenuCallback = object : Callback<DateControl.EntryContextMenuParameter, ContextMenu> {
            override fun call(param: DateControl.EntryContextMenuParameter): ContextMenu {
                val selectedLogs = jfxCalendarView.selections
                        .map { it.userObject as SimpleLog }
                        .map { it._id }
                contextMenu.bindLogs(selectedLogs)
                return contextMenu.root
            }
        }
        // todo incomplete menu actions
        jfxCalendarView.contextMenuCallback = object : Callback<DateControl.ContextMenuParameter, ContextMenu> {
            override fun call(param: DateControl.ContextMenuParameter): ContextMenu {
                val contextMenu = ContextMenu()
                contextMenu.items.add(CalendarMenuItemProvider.provideMenuItemNewItem(param.zonedDateTime, strings, storage))
                if (jfxCalendarView is DetailedDayView) {
                    contextMenu.items.add(CalendarMenuItemProvider.provideMenuItemScale(jfxCalendarView as DetailedDayView, strings))
                }
                if (jfxCalendarView is DetailedWeekView) {
                    contextMenu.items.add(CalendarMenuItemProvider.provideMenuItemScale(jfxCalendarView as DetailedWeekView, strings))
                }
                contextMenu.onAction = object : EventHandler<ActionEvent> {
                    override fun handle(event: ActionEvent) {
                        contextMenu.hide()
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
                return false
            }
        }
        logLoader = CalendarFxLogLoader(
                calendarLoaderListener,
                timeProvider,
                schedulerProvider.io(),
                schedulerProvider.ui()
        )
        calendarUpdater = CalendarFxUpdater(
                calendarUpdateListener,
                schedulerProvider.waitScheduler(),
                schedulerProvider.ui()
        )
        logLoader.onAttach()
        calendarUpdater.onAttach()
        logLoader.load(storage.data)
        jfxCalendarView.selections.addListener(jfxCalSelectionListener)
    }

    @PreDestroy
    fun destroy() {
        jfxCalendarView.selections.removeListener(jfxCalSelectionListener)
        calendarUpdater.onDetach()
        logLoader.onDetach()
        storage.unregister(storageListener)
    }

    //region Listeners

    private val jfxCalSelectionListener = SetChangeListener<Entry<*>> {
        val currentSelection = jfxCalendarView.selections.toList()
        if (currentSelection.isNotEmpty()) {
            val simpleLog = currentSelection.first().userObject as SimpleLog
            this.selectedId = simpleLog._id
        } else {
            this.selectedId = Const.NO_ID
        }
        eventBus.post(EventLogSelection(selectedId))
    }

    private val storageListener: IDataListener<SimpleLog> = object : IDataListener<SimpleLog> {
        override fun onDataChange(data: List<SimpleLog>) {
            logLoader.load(data)
            jfxCalendarView.today = LocalDate.now()
            jfxCalendarView.time = LocalTime.now()
        }
    }

    private val calendarUpdateListener: CalendarFxUpdater.Listener = object : CalendarFxUpdater.Listener {
        override fun onCurrentTimeUpdate(currentTime: LocalTime) {
            jfxCalendarView.today = LocalDate.now()
            jfxCalendarView.time = LocalTime.now()
        }
    }

    private val calendarLoaderListener: CalendarFxLogLoader.View = object : CalendarFxLogLoader.View {
        override fun onCalendarEntries(
                allEntries: List<Entry<SimpleLog>>,
                entriesInSync: List<Entry<SimpleLog>>,
                entriesWaitingForSync: List<Entry<SimpleLog>>,
                entriesInError: List<Entry<SimpleLog>>
        ) {
            val targetDate = storage.targetDate.toLocalDate() // todo: Provide shown date
            jfxCalendarView.date = LocalDate.of(
                    targetDate.year,
                    targetDate.monthOfYear,
                    targetDate.dayOfMonth
            )
            calendarInSync.startBatchUpdates()
            calendarWaitingForSync.startBatchUpdates()
            calendarError.startBatchUpdates()

            calendarInSync.clear()
            calendarInSync.addEntries(entriesInSync)
            calendarWaitingForSync.clear()
            calendarWaitingForSync.addEntries(entriesWaitingForSync)
            calendarError.clear()
            calendarError.addEntries(entriesInError)

            calendarInSync.stopBatchUpdates()
            calendarWaitingForSync.stopBatchUpdates()
            calendarError.stopBatchUpdates()

            selectActiveLog(allEntries)
        }

        override fun onCalendarNoEntries() {
            val targetDate = storage.targetDate.toLocalDate() // todo: Provide shown date
            jfxCalendarView.date = LocalDate.of(
                    targetDate.year,
                    targetDate.monthOfYear,
                    targetDate.dayOfMonth
            )
            calendarInSync.clear()
            calendarWaitingForSync.clear()
            calendarError.clear()
        }

    }

    private fun findEntryByLocalIdOrNull(
            localId: Long,
            entries: List<Entry<SimpleLog>>
    ): Entry<SimpleLog>? {
        return entries.firstOrNull { it.userObject._id == localId }
    }

    private fun selectActiveLog(allEntries: List<Entry<SimpleLog>>) {
        val selection = findEntryByLocalIdOrNull(selectedId, allEntries)
        if (selection != null) {
            jfxCalendarView.select(selection)
        }
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}

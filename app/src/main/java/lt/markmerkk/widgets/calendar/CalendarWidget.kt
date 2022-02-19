package lt.markmerkk.widgets.calendar

import com.calendarfx.model.Calendar
import com.calendarfx.model.CalendarEvent
import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.calendarfx.view.DateControl
import com.calendarfx.view.DayViewBase
import com.calendarfx.view.DetailedDayView
import com.calendarfx.view.DetailedWeekView
import com.google.common.eventbus.Subscribe
import com.jfoenix.controls.JFXSlider
import com.jfoenix.svg.SVGGlyph
import javafx.collections.SetChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Callback
import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.Log.Companion.clone
import lt.markmerkk.entities.Log.Companion.cloneAsNewLocal
import lt.markmerkk.entities.LogEditType
import lt.markmerkk.events.*
import lt.markmerkk.total.TotalGenStringRes
import lt.markmerkk.total.TotalWorkGenerator
import lt.markmerkk.ui_2.views.ContextMenuEditLog
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.ui_2.views.jfxSlider
import lt.markmerkk.utils.CalendarFxLogLoader
import lt.markmerkk.utils.CalendarMenuItemProvider
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.validators.LogChangeValidator
import lt.markmerkk.widgets.MainContainerNavigator
import org.slf4j.LoggerFactory
import rx.observables.JavaFxObservable
import tornadofx.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.WeekFields
import javax.inject.Inject

class CalendarWidget: Fragment() {

    @Inject lateinit var tracker: ITracker
    @Inject lateinit var strings: Strings
    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var logChangeValidator: LogChangeValidator
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var hourGlass: HourGlass
    @Inject lateinit var activeDisplayRepository: ActiveDisplayRepository
    @Inject lateinit var worklogStorage: WorklogStorage

    private lateinit var viewCalendar: DayViewBase
    private lateinit var viewContainer: BorderPane
    private lateinit var viewDragIndicator: VBox
    private lateinit var viewZoomIndicator: VBox
    private lateinit var viewZoomSlider: JFXSlider
    private lateinit var viewInfoLabel: Label

    private lateinit var logLoader: CalendarFxLogLoader

    init {
        Main.component().inject(this)
    }

    private val totalWorkGenerator = TotalWorkGenerator(hourGlass, TotalGenStringRes(), activeDisplayRepository)
    private val mainContainerNavigator = MainContainerNavigator(
            eventBus,
            this,
        activeDisplayRepository
    )
    private lateinit var contextMenu: ContextMenuEditLog

    private val calendarChangeEventHandler: EventHandler<CalendarEvent> = object : EventHandler<CalendarEvent> {
        override fun handle(event: CalendarEvent) {
            if (event.eventType == CalendarEvent.ENTRY_INTERVAL_CHANGED) {
                val calendarEntryStart = event.entry.startMillis
                val calendarEntryEnd = event.entry.endMillis
                val oldLogEntry = event.entry.userObject as Log
                val newLogEntry = oldLogEntry.clone(
                    timeProvider = timeProvider,
                    start = timeProvider.roundDateTime(calendarEntryStart),
                    end = timeProvider.roundDateTime(calendarEntryEnd)
                )
                activeDisplayRepository.update(newLogEntry)
            }
        }
    }
    private val calendarInSync = Calendar().apply {
        setStyle(Calendar.Style.STYLE1)
        isReadOnly = false
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

    private var selectedId: Long = Const.NO_ID
    private var inEditMode: Boolean = false

    override val root: Parent = stackpane {
        viewContainer = borderpane {
            center { }
        }
        viewInfoLabel = label {
            addClass(Styles.emojiText)
            addClass(Styles.labelMini)
            style {
                backgroundColor.add(Color(1.0, 1.0, 1.0, 0.8))
                backgroundRadius.add(box(6.pt))
                backgroundInsets.add(box(2.px))
                fontSize = 12.0.px
                padding = box(
                        vertical = 4.px,
                        horizontal = 8.px
                )
            }
            StackPane.setAlignment(this, Pos.BOTTOM_LEFT)
            isMouseTransparent = true
        }
        viewDragIndicator = vbox(alignment = Pos.BOTTOM_CENTER) {
            isMouseTransparent = true
            label("Drag worklog to move or scale") {
                style {
                    padding = box(vertical = 8.px, horizontal = 14.px)
                    backgroundColor.add(Color.BLACK)
                    fontSize = 14.pt
                }
                textFill = Color.WHITE
            }
            hide()
        }
        hbox(spacing = 4, alignment = Pos.CENTER) {
            StackPane.setAlignment(this, Pos.BOTTOM_RIGHT)
            style {
                padding = box(
                        top = 0.px,
                        left = 0.px,
                        bottom = 6.px,
                        right = 16.px
                )
            }
            isPickOnBounds = true
            maxWidth = 160.0
            maxHeight = 16.0
            jfxButton {
                graphic = graphics.from(Glyph.ZOOM_OUT, Color.BLACK, 12.0)
                action {
                    val currentValue = viewZoomSlider.value
                    viewZoomSlider.value = currentValue - 20.0
                }
            }
            viewZoomSlider = jfxSlider {
                min = 40.0
                max = 200.0
                value = 50.0
            }
            jfxButton {
                graphic = graphics.from(Glyph.ZOOM_IN, Color.BLACK, 16.0)
                action {
                    val currentValue = viewZoomSlider.value
                    viewZoomSlider.value = currentValue + 20.0
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        viewCalendar = when (activeDisplayRepository.displayType) {
            DisplayTypeLength.DAY -> DetailedDayView()
            DisplayTypeLength.WEEK -> DetailedWeekView()
        }
        viewContainer.center = viewCalendar
        viewCalendar.hoursLayoutStrategy = DayViewBase.HoursLayoutStrategy.FIXED_HOUR_HEIGHT
        JavaFxObservable.valuesOf(viewZoomSlider.valueProperty())
                .subscribe({
                    viewCalendar.hourHeight = it.toDouble()
                }, { error ->
                    logger.warn("JFX prop error", error)
                })
        viewZoomSlider.value = 50.0
        contextMenu = ContextMenuEditLog(
            strings,
            graphics,
            eventBus,
            worklogStorage,
            listOf(
                LogEditType.UPDATE,
                LogEditType.CLONE,
                LogEditType.DELETE,
                LogEditType.SPLIT,
                LogEditType.WEBLINK,
                LogEditType.BROWSER
            )
        )
        tracker.sendView(GAStatics.VIEW_CALENDAR_DAY)

        viewCalendar.calendarSources.add(calendarSource)
        if (viewCalendar is com.calendarfx.view.DetailedDayView) {
            val jfxDayView = viewCalendar as com.calendarfx.view.DetailedDayView
            jfxDayView.isShowAllDayView = false
            jfxDayView.isShowAgendaView = false
        }
        if (viewCalendar is com.calendarfx.view.DetailedWeekView) {
            val jfxWeekView = viewCalendar as com.calendarfx.view.DetailedWeekView
            jfxWeekView.isShowAllDayView = false
            jfxWeekView.weekFields = WeekFields.of(DayOfWeek.MONDAY, 7)
        }
        viewCalendar.entryDetailsCallback = calendarEntryDetailsCallback
        viewCalendar.entryContextMenuCallback = calendarEntryContextMenuCallback
        viewCalendar.contextMenuCallback = calendarContextMenuCallback
        viewCalendar.entryFactory = calendarEntryFactory
        viewCalendar.entryEditPolicy = calendarEntryEditPolicy
        logLoader = CalendarFxLogLoader(
                calendarLoaderListener,
                timeProvider,
                schedulerProvider.io(),
                schedulerProvider.ui()
        )
        logLoader.onAttach()
        logLoader.load(activeDisplayRepository.displayLogs)
        viewCalendar.selections.addListener(jfxCalSelectionListener)
        mainContainerNavigator.onAttach()
        eventBus.register(this)
        viewInfoLabel.text = totalWorkGenerator
                .reportTotalWithWorkdayEnd(
                    activeDisplayRepository.displayDateRange.start,
                    activeDisplayRepository.displayDateRange.endAsNextDay
                )
    }

    override fun onUndock() {
        eventBus.unregister(this)
        mainContainerNavigator.onDetach()
        viewCalendar.selections.removeListener(jfxCalSelectionListener)
        logLoader.onDetach()
        super.onUndock()
    }

    //region Events

    @Subscribe
    fun eventClockChange(event: EventClockChange) {
        viewInfoLabel.text = totalWorkGenerator
                .reportTotalWithWorkdayEnd(
                    activeDisplayRepository.displayDateRange.start,
                    activeDisplayRepository.displayDateRange.endAsNextDay
                )
    }

    @Subscribe
    fun eventTickTock(event: EventTickTock) {
        viewCalendar.today = LocalDate.now()
        viewCalendar.time = LocalTime.now()
        viewInfoLabel.text = totalWorkGenerator
                .reportTotalWithWorkdayEnd(
                    activeDisplayRepository.displayDateRange.start,
                    activeDisplayRepository.displayDateRange.endAsNextDay
                )
    }

    @Subscribe
    fun onEditModeChange(event: EventEditMode) {
        this.inEditMode = event.isInEdit
        if (inEditMode) {
            viewDragIndicator.show()
        } else {
            viewDragIndicator.hide()
        }
    }

    @Subscribe
    fun onFocusChange(event: EventFocusChange) {
        if (event.isInFocus) {
            viewCalendar.today = LocalDate.now()
            viewCalendar.time = LocalTime.now()
            viewInfoLabel.text = totalWorkGenerator
                    .reportTotalWithWorkdayEnd(
                        activeDisplayRepository.displayDateRange.start,
                        activeDisplayRepository.displayDateRange.endAsNextDay
                    )
        }
    }

    @Subscribe
    fun onActiveDisplayDataChange(event: EventActiveDisplayDataChange) {
        logLoader.load(event.data)
        viewCalendar.today = LocalDate.now()
        viewCalendar.time = LocalTime.now()
        viewInfoLabel.text = totalWorkGenerator
            .reportTotalWithWorkdayEnd(
                activeDisplayRepository.displayDateRange.start,
                activeDisplayRepository.displayDateRange.endAsNextDay
            )
        eventBus.post(EventLogSelection(Const.NO_ID))
    }

    //endregion

    //region Calendar listeners

    private val calendarEntryDetailsCallback = object : Callback<DateControl.EntryDetailsParameter, Boolean> {
        override fun call(param: DateControl.EntryDetailsParameter): Boolean {
            if (param.inputEvent.eventType != MouseEvent.MOUSE_CLICKED) {
                return true
            }
            if ((param.inputEvent as MouseEvent).clickCount < 2) {
                return true
            }
            val log = param.entry.userObject as Log
            eventBus.post(EventEditLog(LogEditType.UPDATE, listOf(log)))
            return true
        }
    }

    private val calendarEntryContextMenuCallback = object : Callback<DateControl.EntryContextMenuParameter, ContextMenu> {
        override fun call(param: DateControl.EntryContextMenuParameter): ContextMenu {
            val selectedLogs = viewCalendar.selections
                    .map { it.userObject as Log }
                    .map { it.id }
            contextMenu.bindLogs(selectedLogs)
            return contextMenu.root
        }
    }

    private val calendarContextMenuCallback = object : Callback<DateControl.ContextMenuParameter, ContextMenu> {
        override fun call(param: DateControl.ContextMenuParameter): ContextMenu {
            val contextMenu = ContextMenu()
            contextMenu.items.add(
                CalendarMenuItemProvider
                    .provideMenuItemNewItem(
                        param.zonedDateTime,
                        strings,
                        timeProvider,
                        activeDisplayRepository
                    )
            )
            contextMenu.onAction = object : EventHandler<ActionEvent> {
                override fun handle(event: ActionEvent) {
                    contextMenu.hide()
                }
            }
            return contextMenu
        }
    }

    private val calendarEntryFactory = object : Callback<DateControl.CreateEntryParameter, Entry<*>> {
        override fun call(param: DateControl.CreateEntryParameter): Entry<Log>? {
            val startMillis = param.zonedDateTime.toInstant().toEpochMilli()
            val endMillis = param.zonedDateTime.plusHours(1).toInstant().toEpochMilli()
            val log = Log.createAsEmpty(timeProvider = timeProvider)
                .cloneAsNewLocal(
                    timeProvider = timeProvider,
                    start = timeProvider.roundDateTime(startMillis),
                    end = timeProvider.roundDateTime(endMillis)
                )
            activeDisplayRepository.insertOrUpdate(log)
            return null
        }
    }

    private val calendarEntryEditPolicy = object : Callback<DateControl.EntryEditParameter, Boolean> {
        override fun call(param: DateControl.EntryEditParameter): Boolean {
            return inEditMode
        }
    }

    private val jfxCalSelectionListener = SetChangeListener<Entry<*>> {
        val currentSelection = viewCalendar.selections.toList()
        if (currentSelection.isNotEmpty()) {
            val simpleLog = currentSelection.first().userObject as Log
            this.selectedId = simpleLog.id
        } else {
            this.selectedId = Const.NO_ID
        }
        eventBus.post(EventLogSelection(selectedId))
    }

    //endregion

    //region Listeners

    private val calendarLoaderListener: CalendarFxLogLoader.View = object : CalendarFxLogLoader.View {
        override fun onCalendarEntries(
                allEntries: List<Entry<Log>>,
                entriesInSync: List<Entry<Log>>,
                entriesWaitingForSync: List<Entry<Log>>,
                entriesInError: List<Entry<Log>>
        ) {
            viewCalendar.date = TimeProvider.toJavaLocalDate(activeDisplayRepository.displayDateRange.selectDate)
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
            viewCalendar.date = TimeProvider.toJavaLocalDate(activeDisplayRepository.displayDateRange.selectDate)
            calendarInSync.clear()
            calendarWaitingForSync.clear()
            calendarError.clear()
        }

    }

    private fun findEntryByLocalIdOrNull(
            localId: Long,
            entries: List<Entry<Log>>
    ): Entry<Log>? {
        return entries.firstOrNull { it.userObject.id == localId }
    }

    private fun selectActiveLog(allEntries: List<Entry<Log>>) {
        val selection = findEntryByLocalIdOrNull(selectedId, allEntries)
        if (selection != null) {
            viewCalendar.select(selection)
        }
    }

    //endregion

    companion object {
        val logger = LoggerFactory.getLogger(Tags.CALENDAR)!!
    }

}
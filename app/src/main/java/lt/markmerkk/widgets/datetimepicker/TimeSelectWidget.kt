package lt.markmerkk.widgets.datetimepicker

import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import lt.markmerkk.Glyph
import lt.markmerkk.Graphics
import lt.markmerkk.Main
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.Styles
import lt.markmerkk.TimeProvider
import lt.markmerkk.ViewProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventChangeTime
import lt.markmerkk.timeselect.TimeSelectContract
import lt.markmerkk.timeselect.TimeSelectPresenter
import lt.markmerkk.timeselect.entities.TimeSelectRequest
import lt.markmerkk.timeselect.entities.TimeSelectResult
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.widgets.datetimepicker.listitems.TimePickItemFragment
import lt.markmerkk.widgets.datetimepicker.listitems.TimePickViewModel
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.hbox
import tornadofx.label
import tornadofx.listview
import tornadofx.px
import tornadofx.style
import tornadofx.top
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

class TimeSelectWidget : Fragment(), TimeSelectContract.View {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var resultDispatcher: ResultDispatcher

    private lateinit var viewListHour: ListView<TimePickViewModel>
    private lateinit var viewListMinute: ListView<TimePickViewModel>

    private lateinit var request: TimeSelectRequest

    private val obsHours = hourSelections()
        .map { TimePickViewModel(it.hourOfDay.toString()) }
        .asReversed()
        .asObservable()
    private val obsMinutes = minuteSelections()
        .map { TimePickViewModel(it.minuteOfHour.toString()) }
        .asReversed()
        .asObservable()

    init {
        Main.component().inject(this)
    }

    private val presenter: TimeSelectContract.Presenter = TimeSelectPresenter(
        view = object : ViewProvider<TimeSelectContract.View>() {
            override fun get(): TimeSelectContract.View? = this@TimeSelectWidget
        }
    )

    override val root: Parent = borderpane {
        addClass(Styles.dialogContainer)
        style {
            minWidth = 300.px
            prefWidth = 300.px
            prefHeight = 260.px
        }
        top {
            hbox(spacing = 10, alignment = Pos.TOP_LEFT) {
                label("Time picker") {
                    addClass(Styles.dialogHeader)
                }
            }
        }
        center {
            vbox(spacing = 4.0) {
                hbox(alignment = Pos.CENTER) {
                    label("Hours / Minutes")
                }
                hbox(spacing = 4.0, alignment = Pos.CENTER) {
                    vbox(spacing = 4.0, alignment = Pos.CENTER) {
                        jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FORWARD, Color.BLACK, 10.0, 12.0, -90.0)
                            setOnAction {
                                presenter.plusHour(HOUR_JUMP)
                            }
                        }
                        jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FORWARD, Color.BLACK, 10.0, 12.0, 90.0)
                            setOnAction {
                                presenter.minusHour(HOUR_JUMP)
                            }
                        }
                    }
                    viewListHour = listview(obsHours) {
                        maxWidth = 50.0
                        vgrow = Priority.ALWAYS
                        cellFragment(TimePickItemFragment::class)
                    }
                    viewListMinute = listview(obsMinutes) {
                        maxWidth = 50.0
                        vgrow = Priority.ALWAYS
                        cellFragment(TimePickItemFragment::class)
                    }
                    vbox(spacing = 4.0, alignment = Pos.CENTER) {
                        jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FAST_FORWARD, Color.BLACK, 16.0, 12.0, -90.0)
                            setOnAction {
                                presenter.plusMinute(MINUTE_JUMP_FAST)
                            }
                        }
                        jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FORWARD, Color.BLACK, 10.0, 12.0, -90.0)
                            setOnAction {
                                presenter.plusMinute(MINUTE_JUMP)
                            }
                        }
                        jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FORWARD, Color.BLACK, 10.0, 12.0, 90.0)
                            setOnAction {
                                presenter.minusMinute(MINUTE_JUMP)
                            }
                        }
                        jfxButton {
                            graphic = graphics.from(Glyph.ARROW_FAST_FORWARD, Color.BLACK, 16.0, 10.0, 90.0)
                            setOnAction {
                                presenter.minusMinute(MINUTE_JUMP_FAST)
                            }
                        }
                    }
                }
                label("Original time selection: 14:10") {
                    style {
                        addClass(Styles.labelMini)
                    }
                }
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerActionsButtons)
                jfxButton("Close".toUpperCase()) {
                    setOnAction { close() }
                }
                jfxButton("Select".toUpperCase()) {
                    setOnAction {
                        resultDispatcher.publish(
                            key = RESULT_DISPATCH_KEY_RESULT,
                            resultEntity = TimeSelectResult
                                .withNewValue(request, presenter.timeSelection)
                        )
                        eventBus.post(EventChangeTime)
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        presenter.onAttach()
        l.debug("onDock()")
        this.request = resultDispatcher
            .consume(RESULT_DISPATCH_KEY_PRESELECT, TimeSelectRequest::class.java) ?: TimeSelectRequest.asDefault()
        presenter.selectTime(request.timeSelection)
        viewListHour.selectionModel
            .selectedItemProperty()
            .addListener(listenerTimeSelectChangeHour)
        viewListMinute.selectionModel
            .selectedItemProperty()
            .addListener(listenerTimeSelectChangeMinute)
    }

    override fun onUndock() {
        viewListHour.selectionModel
            .selectedItemProperty()
            .removeListener(listenerTimeSelectChangeHour)
        viewListMinute.selectionModel
            .selectedItemProperty()
            .removeListener(listenerTimeSelectChangeMinute)
        presenter.onDetach()
        l.debug("onUndock()")
        super.onUndock()
    }

    override fun renderSelection(hour: Int, minute: Int) {
        val obsHour = obsHours.firstOrNull { it.timeAsProperty.get() == hour.toString() }
        if (obsHour != null) {
            viewListHour.selectionModel.select(obsHour)
            viewListHour.scrollTo(obsHour)
        }
        val obsMinute = obsMinutes.firstOrNull { it.timeAsProperty.get() == minute.toString() }
        if (obsMinute != null) {
            viewListMinute.selectionModel.select(obsMinute)
            viewListMinute.scrollTo(obsMinute)
        }
    }

    //region Listeners

    private val listenerTimeSelectChangeHour = ChangeListener<TimePickViewModel> { _, _, newValue ->
        presenter.selectHour(newValue.timeAsProperty.get().toInt(), render = false)
    }

    private val listenerTimeSelectChangeMinute = ChangeListener<TimePickViewModel> { _, _, newValue ->
        presenter.selectMinute(newValue.timeAsProperty.get().toInt(), render = false)
    }

    //endregion

    companion object {
        const val RESULT_DISPATCH_KEY_PRESELECT = "747b6ef3-5e5e-4c5c-bf38-c03c87fa3919"
        const val RESULT_DISPATCH_KEY_RESULT = "dfdfc9bd-c059-4970-bc2f-ca37f79a145e"
        val l = LoggerFactory.getLogger(TimeSelectWidget::class.java)!!

        const val TIME_SHORT_FORMAT = "HH:mm"
        const val MINUTE_JUMP = 1
        const val MINUTE_JUMP_FAST = 10
        const val HOUR_JUMP = 1
        const val HOUR_JUMP_FAST = 3
        val shortFormat = DateTimeFormat.forPattern(TIME_SHORT_FORMAT)!!

        fun hourSelections(): List<LocalTime> {
            val timeSelections = mutableListOf<LocalTime>()
            val startTime = LocalDateTime(1970, 1, 1, 0, 0, 0)
            val endTime = startTime.plusDays(1)
            var currentTime = startTime
            do {
                timeSelections.add(currentTime.toLocalTime())
                currentTime = currentTime.plusHours(1)
            } while(currentTime.isBefore(endTime))
            return timeSelections.toList()
        }

        fun minuteSelections(): List<LocalTime> {
            val timeSelections = mutableListOf<LocalTime>()
            val startTime = LocalDateTime(1970, 1, 1, 0, 0, 0)
            val endTime = startTime.plusHours(1)
            var currentTime = startTime
            do {
                timeSelections.add(currentTime.toLocalTime())
                currentTime = currentTime.plusMinutes(1)
            } while(currentTime.isBefore(endTime))
            return timeSelections.toList()
        }
    }

}

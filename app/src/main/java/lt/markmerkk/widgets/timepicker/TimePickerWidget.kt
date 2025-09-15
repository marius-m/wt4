package lt.markmerkk.widgets.timepicker

import com.jfoenix.svg.SVGGlyph
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
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
import lt.markmerkk.timeselect.TimePickerContract
import lt.markmerkk.timeselect.TimePickerPresenter
import lt.markmerkk.timeselect.entities.TimeSelectRequest
import lt.markmerkk.timeselect.entities.TimeSelectResult
import lt.markmerkk.ui_2.views.jfxButton
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.widgets.timepicker.listitems.TimePickItemFragment
import lt.markmerkk.widgets.timepicker.listitems.TimePickViewModel
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.box
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
import lt.markmerkk.ui_2.BaseFragment

class TimePickerWidget : BaseFragment(), TimePickerContract.View {

    @Inject lateinit var graphics: Graphics<SVGGlyph>
    @Inject lateinit var eventBus: WTEventBus
    @Inject lateinit var timeProvider: TimeProvider
    @Inject lateinit var resultDispatcher: ResultDispatcher

    private lateinit var viewTitle: Label
    private lateinit var viewSubtitle: Label
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

    private val presenter: TimePickerContract.Presenter = TimePickerPresenter(
        view = object : ViewProvider<TimePickerContract.View>() {
            override fun get(): TimePickerContract.View? = this@TimePickerWidget
        }
    )

    override val root: Parent = borderpane {
        style {
            minWidth = 300.px
            maxWidth = 300.px
            minHeight = 340.px
            maxHeight = 340.px
        }
        top {
            vbox(alignment = Pos.BOTTOM_LEFT) {
                addClass(Styles.dialogHeaderContainerColorful)
                viewTitle = label("") {
                    addClass(Styles.dialogH1TextColorful)
                    style {
                        textFill = Styles.cTextHeaderColorful
                        padding = box(
                            top = 0.px,
                            left = 24.px,
                            right = 0.px,
                            bottom = 0.px
                        )
                    }
                }
                viewSubtitle = label("") {
                    addClass(Styles.dialogH9TextColorful)
                    style {
                        textFill = Styles.cTextHeaderColorful
                        padding = box(
                            top = 0.px,
                            left = 24.px,
                            right = 0.px,
                            bottom = 12.px
                        )
                    }
                }
            }
        }
        center {
            vbox(spacing = 4.0) {
                addClass(Styles.dialogContainerColorContent)
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
            }
        }
        bottom {
            hbox(alignment = Pos.CENTER_RIGHT, spacing = 4) {
                addClass(Styles.dialogContainerColorActionsButtons)
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
                jfxButton("Close".toUpperCase()) {
                    setOnAction { close() }
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
        viewSubtitle.text = "Original: ${LogFormatters.formatTime.print(request.timeSelection)}"
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

    override fun renderHeader(localTime: LocalTime) {
        viewTitle.text = LogFormatters.formatTime.print(localTime)
    }

    override fun renderSelection(localTime: LocalTime) {
        val obsHour = obsHours.firstOrNull { it.timeAsProperty.get() == localTime.hourOfDay.toString() }
        if (obsHour != null) {
            viewListHour.selectionModel.select(obsHour)
            viewListHour.scrollTo(obsHour)
        }
        val obsMinute = obsMinutes.firstOrNull { it.timeAsProperty.get() == localTime.minuteOfHour.toString() }
        if (obsMinute != null) {
            viewListMinute.selectionModel.select(obsMinute)
            viewListMinute.scrollTo(obsMinute)
        }
    }

    //region Listeners

    private val listenerTimeSelectChangeHour = ChangeListener<TimePickViewModel> { _, _, newValue ->
        presenter.selectHour(newValue.timeAsProperty.get().toInt())
    }

    private val listenerTimeSelectChangeMinute = ChangeListener<TimePickViewModel> { _, _, newValue ->
        presenter.selectMinute(newValue.timeAsProperty.get().toInt())
    }

    //endregion

    companion object {
        const val RESULT_DISPATCH_KEY_PRESELECT = "747b6ef3-5e5e-4c5c-bf38-c03c87fa3919"
        const val RESULT_DISPATCH_KEY_RESULT = "dfdfc9bd-c059-4970-bc2f-ca37f79a145e"
        val l = LoggerFactory.getLogger(TimePickerWidget::class.java)!!

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

package lt.markmerkk.ui_2

import com.jfoenix.controls.*
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.util.StringConverter
import lt.markmerkk.Main
import lt.markmerkk.mvp.ClockEditMVP
import lt.markmerkk.mvp.ClockEditPresenterImpl
import lt.markmerkk.mvp.TimeQuickModifier
import lt.markmerkk.mvp.TimeQuickModifierImpl
import lt.markmerkk.utils.hourglass.HourGlass
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class ClockEditController : Initializable, ClockEditMVP.View {

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    @FXML lateinit var jfxButtonDismiss: JFXButton
    @FXML lateinit var jfxDateFrom: JFXDatePicker
    @FXML lateinit var jfxTimeFrom: JFXTimePicker
    @FXML lateinit var jfxDateTo: JFXDatePicker
    @FXML lateinit var jfxTimeTo: JFXTimePicker
    @FXML lateinit var jfxHint: Label
    @FXML lateinit var jfxSubtractFrom: JFXButton
    @FXML lateinit var jfxSubtractTo: JFXButton
    @FXML lateinit var jfxAppendFrom: JFXButton
    @FXML lateinit var jfxAppendTo: JFXButton

    @Inject lateinit var hourglass: HourGlass

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")!!
    private val dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")!!

    private lateinit var clockEditPresenter: ClockEditMVP.Presenter
    private lateinit var timeQuickModifier: TimeQuickModifier

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)

        jfxButtonDismiss.setOnAction {
            jfxDialog.close()
        }
        val timeQuickModifierListener: TimeQuickModifier.Listener = object : TimeQuickModifier.Listener {
            override fun onTimeModified(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
                clockEditPresenter.updateDateTime(
                        startDateTime.toLocalDate(),
                        startDateTime.toLocalTime(),
                        endDateTime.toLocalDate(),
                        endDateTime.toLocalTime()
                )
            }
        }
        timeQuickModifier = TimeQuickModifierImpl(
                timeQuickModifierListener
        )
        clockEditPresenter = ClockEditPresenterImpl(this, hourglass)
        clockEditPresenter.onAttach()

        jfxDateFrom.converter = dateConverter
        jfxTimeFrom.converter = timeConverter
        jfxTimeFrom.setIs24HourView(true)
        jfxDateTo.converter = dateConverter
        jfxTimeTo.converter = timeConverter
        jfxTimeTo.setIs24HourView(true)

        jfxDateFrom.valueProperty().addListener(startDateChangeListener)
        jfxTimeFrom.valueProperty().addListener(startTimeChangeListener)
        jfxDateTo.valueProperty().addListener(endDateChangeListener)
        jfxTimeTo.valueProperty().addListener(endTimeChangeListener)
        jfxSubtractFrom.setOnAction {
            timeQuickModifier.subtractStartTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
        jfxAppendFrom.setOnAction {
            timeQuickModifier.appendStartTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
        jfxSubtractTo.setOnAction {
            timeQuickModifier.subtractEndTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
        jfxAppendTo.setOnAction {
            timeQuickModifier.appendEndTime(
                    LocalDateTime.of(
                            jfxDateFrom.value,
                            jfxTimeFrom.value
                    ),
                    LocalDateTime.of(
                            jfxDateTo.value,
                            jfxTimeTo.value
                    )
            )
        }
    }

    @PreDestroy
    fun destroy() {
        jfxTimeTo.valueProperty().removeListener(endTimeChangeListener)
        jfxDateTo.valueProperty().removeListener(endDateChangeListener)
        jfxTimeFrom.valueProperty().removeListener(startTimeChangeListener)
        jfxDateFrom.valueProperty().removeListener(startDateChangeListener)
        clockEditPresenter.onDetach()
    }

    //region MVP Impl

    override fun onDateChange(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
        jfxDateFrom.value = startDateTime.toLocalDate()
        jfxTimeFrom.value = startDateTime.toLocalTime()

        jfxDateTo.value = endDateTime.toLocalDate()
        jfxTimeTo.value = endDateTime.toLocalTime()
    }

    override fun onHintChange(hint: String) {
        jfxHint.text = hint
    }

    //endregion

    //region Listeners

    private val dateConverter: StringConverter<LocalDate> = object : StringConverter<LocalDate>() {
        override fun toString(date: LocalDate): String {
            return date.format(dateFormatter)
        }

        override fun fromString(string: String): LocalDate {
            return LocalDate.parse(string)
        }

    }

    private val timeConverter: StringConverter<LocalTime> = object : StringConverter<LocalTime>() {
        override fun toString(date: LocalTime): String {
            return date.format(timeFormatter)
        }

        override fun fromString(string: String): LocalTime {
            return LocalTime.parse(string)
        }
    }

    private val startDateChangeListener = ChangeListener<LocalDate> { observable, oldValue, newValue ->
        clockEditPresenter.updateDateTime(
                startDate = newValue,
                startTime = jfxTimeFrom.value,
                endDate = jfxDateTo.value,
                endTime = jfxTimeTo.value
        )
    }

    private val startTimeChangeListener = ChangeListener<LocalTime> { observable, oldValue, newValue ->
        clockEditPresenter.updateDateTime(
                startDate = jfxDateFrom.value,
                startTime = newValue,
                endDate = jfxDateTo.value,
                endTime = jfxTimeTo.value
        )
    }

    private val endDateChangeListener = ChangeListener<LocalDate> { observable, oldValue, newValue ->
        clockEditPresenter.updateDateTime(
                startDate = jfxDateFrom.value,
                startTime = jfxTimeFrom.value,
                endDate = newValue,
                endTime = jfxTimeTo.value
        )
    }

    private val endTimeChangeListener = ChangeListener<LocalTime> { observable, oldValue, newValue ->
        clockEditPresenter.updateDateTime(
                startDate = jfxDateFrom.value,
                startTime = jfxTimeFrom.value,
                endDate = jfxDateTo.value,
                endTime = newValue
        )
    }

    //endregion

}
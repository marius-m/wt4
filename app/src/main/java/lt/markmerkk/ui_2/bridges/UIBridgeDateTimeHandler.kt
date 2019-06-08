package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTimePicker
import javafx.beans.value.ChangeListener
import javafx.util.StringConverter
import lt.markmerkk.TimeProvider
import lt.markmerkk.mvp.ClockEditMVP
import lt.markmerkk.mvp.LogEditService
import lt.markmerkk.mvp.TimeQuickModifier
import lt.markmerkk.ui.UILifecycleBridge
import org.joda.time.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Represents controls / listeners to control date and time
 * Lifecycle: [onAttach], [onDetach]
 */
class UIBridgeDateTimeHandler(
        private val jfxDateFrom: JFXDatePicker,
        private val jfxTimeFrom: JFXTimePicker,
        private val jfxDateTo: JFXDatePicker,
        private val jfxTimeTo: JFXTimePicker,
        private val timeProvider: TimeProvider,
        private val timeQuickModifier: TimeQuickModifier?,
        private val clockEditPresenter: ClockEditMVP.Presenter?,
        private val logEditService: LogEditService?
) : UILifecycleBridge {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")!!
    private val dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")!!

    override fun onAttach() {
        jfxDateFrom.converter = dateConverter
        jfxTimeFrom.converter = timeConverter
        jfxTimeFrom.is24HourView = true
        jfxDateTo.converter = dateConverter
        jfxTimeTo.converter = timeConverter
        jfxTimeTo.is24HourView = true

        jfxDateFrom.valueProperty().addListener(startDateChangeListener)
        jfxTimeFrom.valueProperty().addListener(startTimeChangeListener)
        jfxDateTo.valueProperty().addListener(endDateChangeListener)
        jfxTimeTo.valueProperty().addListener(endTimeChangeListener)
    }

    override fun onDetach() {
        jfxTimeTo.valueProperty().removeListener(endTimeChangeListener)
        jfxDateTo.valueProperty().removeListener(endDateChangeListener)
        jfxTimeFrom.valueProperty().removeListener(startTimeChangeListener)
        jfxDateFrom.valueProperty().removeListener(startDateChangeListener)
    }

    fun changeDate(
            startDateTime: DateTime,
            endDateTime: DateTime
    ) {
        jfxDateFrom.value = TimeProvider.toJavaLocalDate(startDateTime)
        jfxTimeFrom.value = TimeProvider.toJavaLocalTime(startDateTime)
        jfxDateTo.value = TimeProvider.toJavaLocalDate(endDateTime)
        jfxTimeTo.value = TimeProvider.toJavaLocalTime(endDateTime)
    }

    fun enable() {
        jfxDateFrom.isEditable = true
        jfxTimeFrom.isEditable = true
        jfxDateTo.isEditable = true
        jfxTimeTo.isEditable = true
    }

    fun disable() {
        jfxDateFrom.isEditable = false
        jfxTimeFrom.isEditable = false
        jfxDateTo.isEditable = false
        jfxTimeTo.isEditable = false
    }

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
        val start = timeProvider.toJodaDateTime(newValue, jfxTimeFrom.value)
        val end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value)
        clockEditPresenter?.updateDateTime(start, end)
        logEditService?.updateDateTime(start, end)
    }

    private val startTimeChangeListener = ChangeListener<LocalTime> { observable, oldValue, newValue ->
        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, newValue)
        val end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value)
        clockEditPresenter?.updateDateTime(start, end)
        logEditService?.updateDateTime(start, end)
    }

    private val endDateChangeListener = ChangeListener<LocalDate> { observable, oldValue, newValue ->
        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value)
        val end = timeProvider.toJodaDateTime(newValue, jfxTimeTo.value)
        clockEditPresenter?.updateDateTime(start, end)
        logEditService?.updateDateTime(start, end)
    }

    private val endTimeChangeListener = ChangeListener<LocalTime> { observable, oldValue, newValue ->
        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value)
        val end = timeProvider.toJodaDateTime(jfxDateTo.value, newValue)
        clockEditPresenter?.updateDateTime(start, end)
        logEditService?.updateDateTime(start, end)
    }

    //endregion

}
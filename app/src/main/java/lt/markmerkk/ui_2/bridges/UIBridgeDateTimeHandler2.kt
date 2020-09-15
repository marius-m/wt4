package lt.markmerkk.ui_2.bridges

import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXDatePicker
import javafx.util.StringConverter
import lt.markmerkk.TimeProvider
import lt.markmerkk.ui.UILifecycleBridge
import lt.markmerkk.utils.LogFormatters
import lt.markmerkk.validators.TimeChangeValidator
import org.joda.time.DateTime
import tornadofx.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Represents controls / listeners to control date and time
 * Lifecycle: [onAttach], [onDetach]
 */
class UIBridgeDateTimeHandler2(
        private val jfxDateFrom: JFXDatePicker,
        private val jfxTimeFrom: JFXComboBox<String>,
        private val jfxDateTo: JFXDatePicker,
        private val jfxTimeTo: JFXComboBox<String>,
        private val timeProvider: TimeProvider,
        private val dateTimeUpdater: DateTimeUpdater
) : UILifecycleBridge {

    private val timeFormatter = DateTimeFormatter.ofPattern(LogFormatters.TIME_SHORT_FORMAT)!!
    private val dateFormatter = DateTimeFormatter.ofPattern(LogFormatters.DATE_SHORT_FORMAT)!!
    private val timeChangeValidator = TimeChangeValidator

    private val timeItemsFrom = mutableListOf<String>().toObservable()
    private val timeItemsTo = mutableListOf<String>().toObservable()

    override fun onAttach() {
        jfxDateFrom.converter = dateConverter
//        jfxTimeFrom.converter = timeConverter
//        jfxTimeFrom.is24HourView = true
        jfxDateTo.converter = dateConverter
//        jfxTimeTo.converter = timeConverter
//        jfxTimeTo.is24HourView = true

//        jfxDateFrom.valueProperty().addListener(startDateChangeListener)
//        jfxTimeFrom.valueProperty().addListener(startTimeChangeListener)
//        jfxDateTo.valueProperty().addListener(endDateChangeListener)
//        jfxTimeTo.valueProperty().addListener(endTimeChangeListener)
        jfxTimeFrom.items = timeItemsFrom
        jfxTimeTo.items = timeItemsTo
        val now = timeProvider.jNow()
        val timeFroms: List<String> = generateTime(
                timeFrom = LocalTime.MIDNIGHT,
                timeTo = now.toLocalTime()
        ).map { timeFormatter.format(it) }
        val timeTos: List<String> = generateTime(
                timeFrom = now.toLocalTime(),
                timeTo = LocalTime.of(23, 59)
        ).map { timeFormatter.format(it) }
        timeItemsFrom.addAll(timeFroms)
        timeItemsTo.addAll(timeTos)
    }

    override fun onDetach() {
//        jfxTimeTo.valueProperty().removeListener(endTimeChangeListener)
//        jfxDateTo.valueProperty().removeListener(endDateChangeListener)
//        jfxTimeFrom.valueProperty().removeListener(startTimeChangeListener)
//        jfxDateFrom.valueProperty().removeListener(startDateChangeListener)
    }

    fun changeDate(
            startDateTime: DateTime,
            endDateTime: DateTime
    ) {
        jfxDateFrom.value = TimeProvider.toJavaLocalDate(startDateTime)
//        jfxTimeFrom.value = TimeProvider.toJavaLocalTime(startDateTime)
        jfxDateTo.value = TimeProvider.toJavaLocalDate(endDateTime)
//        jfxTimeTo.value = TimeProvider.toJavaLocalTime(endDateTime)
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

//    private val timeConverter: StringConverter<LocalTime> = object : StringConverter<LocalTime>() {
//        override fun toString(date: LocalTime): String {
//            return date.format(timeFormatter)
//        }
//
//        override fun fromString(string: String): LocalTime {
//            return LocalTime.parse(string)
//        }
//    }

//    private val startDateChangeListener = ChangeListener<LocalDate> { _, _, newValue ->
//        val start = timeProvider.toJodaDateTime(newValue, jfxTimeFrom.value)
//        val end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value)
//        val newTimeGap = timeChangeValidator.changeStart(start, end)
//        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
//    }
//
//    private val startTimeChangeListener = ChangeListener<LocalTime> { _, _, newValue ->
//        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, newValue)
//        val end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value)
//        val newTimeGap = timeChangeValidator.changeStart(start, end)
//        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
//    }
//
//    private val endDateChangeListener = ChangeListener<LocalDate> { _, _, newValue ->
//        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value)
//        val end = timeProvider.toJodaDateTime(newValue, jfxTimeTo.value)
//        val newTimeGap = timeChangeValidator.changeEnd(start, end)
//        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
//    }
//
//    private val endTimeChangeListener = ChangeListener<LocalTime> { _, _, newValue ->
//        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value)
//        val end = timeProvider.toJodaDateTime(jfxDateTo.value, newValue)
//        val newTimeGap = timeChangeValidator.changeEnd(start, end)
//        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
//    }

    //endregion

    interface DateTimeUpdater {
        fun updateDateTime(start: DateTime, end: DateTime)
    }

    companion object {

        fun generateTime(timeFrom: LocalTime, timeTo: LocalTime): List<LocalTime> {
            if (timeFrom.isAfter(timeTo)) {
                return emptyList()
            }
            val times = mutableSetOf<LocalTime>()
            val start = LocalDate.of(1970, 1, 1)
            val timeFromDt = LocalDateTime.of(start, timeFrom)
            val timeToDt = LocalDateTime.of(start, timeTo)
            var timeTick = timeFromDt
            while (timeTick.isBefore(timeToDt) || timeTick == timeToDt) {
                times.add(timeTick.toLocalTime())
                timeTick = timeTick.plusMinutes(30)
            }
            times.add(timeTo)
            return times.toList()
        }

    }

}
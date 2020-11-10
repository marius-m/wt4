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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Represents controls / listeners to control date and time
 * Lifecycle: [onAttach], [onDetach]
 */
class UIBridgeDateTimeHandler2(
        private val jfxDateFrom: JFXDatePicker,
        private val jfxTimeFrom: JFXComboBox<LocalTime>,
        private val jfxDateTo: JFXDatePicker,
        private val jfxTimeTo: JFXComboBox<LocalTime>,
        private val timeProvider: TimeProvider,
        private val dateTimeUpdater: DateTimeUpdater
) : UILifecycleBridge {

    private val timeFormatter = DateTimeFormatter.ofPattern(LogFormatters.TIME_SHORT_FORMAT)!!
    private val dateFormatter = DateTimeFormatter.ofPattern(LogFormatters.DATE_SHORT_FORMAT)!!
    private val timeChangeValidator = TimeChangeValidator

    private val timeItemsFrom = mutableListOf<LocalTime>().toObservable()
    private val timeItemsTo = mutableListOf<LocalTime>().toObservable()

    override fun onAttach() {
        jfxDateFrom.converter = dateConverter
        jfxTimeFrom.converter = timeConverter
        jfxDateTo.converter = dateConverter
        jfxTimeTo.converter = timeConverter

        jfxDateFrom.valueProperty().addListener(startDateChangeListener)
        jfxTimeFrom.valueProperty().addListener(startTimeChangeListener)
        jfxDateTo.valueProperty().addListener(endDateChangeListener)
        jfxTimeTo.valueProperty().addListener(endTimeChangeListener)
        jfxTimeFrom.items = timeItemsFrom
        jfxTimeTo.items = timeItemsTo
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
        jfxDateTo.value = TimeProvider.toJavaLocalDate(endDateTime)
        changeTimePickerValues(
                startTime = TimeProvider.toJavaLocalTime(startDateTime),
                endTime = TimeProvider.toJavaLocalTime(endDateTime)
        )
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

    private fun changeTimePickerValues(startTime: LocalTime, endTime: LocalTime) {
        val timeFroms: List<LocalTime> = generateTime(
                timeFrom = LocalTime.MIDNIGHT,
                timeTo = endTime
        )
        val timeTos: List<LocalTime> = generateTime(
                timeFrom = startTime,
                timeTo = LocalTime.of(23, 59)
        )
        timeItemsFrom.clear()
        timeItemsFrom.addAll(timeFroms)
        timeItemsTo.clear()
        timeItemsTo.addAll(timeTos)
        jfxTimeFrom.value = startTime
        jfxTimeTo.value = endTime
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

    private val startDateChangeListener = ChangeListener<LocalDate> { _, _, newValue ->
        val start = timeProvider.toJodaDateTime(newValue, jfxTimeFrom.value)
        val end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value)
        val newTimeGap = timeChangeValidator.changeStart(start, end)
        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
    }

    private val startTimeChangeListener = ChangeListener<LocalTime> { _, _, newValue ->
        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, newValue)
        val end = timeProvider.toJodaDateTime(jfxDateTo.value, jfxTimeTo.value)
        val newTimeGap = timeChangeValidator.changeStart(start, end)
        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
    }

    private val endDateChangeListener = ChangeListener<LocalDate> { _, _, newValue ->
        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value)
        val end = timeProvider.toJodaDateTime(newValue, jfxTimeTo.value)
        val newTimeGap = timeChangeValidator.changeEnd(start, end)
        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
    }

    private val endTimeChangeListener = ChangeListener<LocalTime> { _, _, newValue ->
        val start = timeProvider.toJodaDateTime(jfxDateFrom.value, jfxTimeFrom.value)
        val end = timeProvider.toJodaDateTime(jfxDateTo.value, newValue)
        val newTimeGap = timeChangeValidator.changeEnd(start, end)
        dateTimeUpdater.updateDateTime(newTimeGap.start, newTimeGap.end)
    }

    //endregion

    interface DateTimeUpdater {
        fun updateDateTime(start: DateTime, end: DateTime)
    }

    companion object {

        /**
         * Generates default time ranges from 0:00 to 23:59
         */
        fun defaultTimeRanges(): List<LocalTime> {
            val times = mutableListOf<LocalTime>()
            val timeEnd = LocalTime.of(23, 30)
            var timeTick = LocalTime.of(0, 0)
            while (timeTick.isBefore(timeEnd)) {
                times.add(timeTick)
                timeTick = timeTick.plusMinutes(30)
            }
            times.add(timeEnd)
            times.add(LocalTime.of(23, 59))
            return times.toList()
        }

        /**
         * Generates time with preset intervals in between (30 minutes)
         * Includes time provided [timeFrom] and [timeTo]
         */
        fun generateTime(timeFrom: LocalTime, timeTo: LocalTime): List<LocalTime> {
            if (timeFrom.isAfter(timeTo)) {
                return emptyList()
            }
            val availableTimes = defaultTimeRanges()
            val timesInRange = availableTimes.filter { time ->
                time.isAfter(timeFrom)
                        && time.isBefore(timeTo)
            }
            val times = mutableSetOf(timeFrom)
            times.addAll(timesInRange)
            times.add(timeTo)
            return times.toList()
        }

    }

}
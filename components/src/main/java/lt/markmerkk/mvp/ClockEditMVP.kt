package lt.markmerkk.mvp

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface ClockEditMVP {

    interface View {
        fun onDateChange(
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime
        )
        fun onHintChange(
                hint: String
        )
    }

    interface Presenter {
        fun onAttach()
        fun onDetach()

        fun updateDateTime(
                startDate: LocalDate,
                startTime: LocalTime,
                endDate: LocalDate,
                endTime: LocalTime
        )
    }

}
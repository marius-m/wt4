package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class LogEditServiceImpl(
        private val listener: LogEditService.Listener,
        private val simpleLog: SimpleLog
) : LogEditService {
    override fun updateDateTime(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime
    ) {
    }

    override fun saveEntity(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            ticket: String,
            comment: String
    ) {

    }

    override fun onAttach() {
        listener.onDataChange(
                LocalDateTime.now(),
                LocalDateTime.now(),
                "test_ticket",
                "test_comment"
        )
    }

    override fun onDetach() {
    }

}
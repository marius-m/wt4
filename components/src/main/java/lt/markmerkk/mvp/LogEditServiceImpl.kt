package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import java.time.LocalDateTime
import java.time.LocalTime

class LogEditServiceImpl(
        private val listener: LogEditService.Listener,
        private val simpleLog: SimpleLog
) : LogEditService {
    override fun updateDateTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
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
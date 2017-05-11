package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import java.time.*

class LogEditServiceImpl(
        private val logEditInteractor: LogEditInteractor,
        private val listener: LogEditService.Listener,
        private var currentEntity: SimpleLog
) : LogEditService {
    override fun updateDateTime(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime
    ) {
        val startInDateTime = LocalDateTime.of(startDate, startTime)
        val endInDateTime = LocalDateTime.of(endDate, endTime)
        try {
            currentEntity = logEditInteractor.update(currentEntity, startInDateTime, endInDateTime)
            listener.onDurationChange(currentEntity.prettyDuration)
            listener.onEnableSaving()
        } catch(e: IllegalArgumentException) {
            listener.onDurationChange("Invalid duration")
            listener.onDisableSaving()
        }
    }

    override fun saveEntity(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            ticket: String,
            comment: String
    ) {

    }

    override fun onAttach() {
        render(currentEntity)
    }

    override fun onDetach() {
    }

    /**
     * Triggers according functions to show on screen
     */
    private fun render(entity: SimpleLog) {
        val start = Instant.ofEpochMilli(entity.start).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val end = Instant.ofEpochMilli(entity.end).atZone(ZoneId.systemDefault()).toLocalDateTime()
        listener.onDataChange(
                start,
                end,
                currentEntity.task ?: "",
                currentEntity.comment ?: ""
        )
        updateDateTime(
                start.toLocalDate(),
                start.toLocalTime(),
                end.toLocalDate(),
                end.toLocalTime()
        )
    }

}
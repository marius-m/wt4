package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
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
            currentEntity = logEditInteractor.updateDateTime(currentEntity, startInDateTime, endInDateTime)
            listener.onDurationChange(currentEntity.prettyDuration)
            listener.onEnableSaving()
        } catch(e: IllegalArgumentException) {
            listener.onDurationChange("Invalid duration")
            listener.onDisableSaving()
        }
    }

    override fun saveEntity(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime,
            task: String,
            comment: String
    ) {
        val startInDateTime = LocalDateTime.of(startDate, startTime)
        val endInDateTime = LocalDateTime.of(endDate, endTime)
        try {
            currentEntity = logEditInteractor.updateTimeConvenience(
                    currentEntity,
                    startInDateTime,
                    endInDateTime,
                    task,
                    comment
            )
            logEditInteractor.save(currentEntity)
            listener.onEntitySaveComplete()
        } catch(e: IllegalArgumentException) {
            listener.onEntitySaveFail(e)
        }
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
        printNotificationIfNeeded(currentEntity)
    }

    /**
     * Generates a generic type of notification for the user
     */
    private fun printNotificationIfNeeded(entity: SimpleLog) {
        if (entity.id > 0) {
            listener.onGenericNotification("Worklog is already in sync with JIRA")
            listener.onDisableInput()
            listener.onDisableSaving()
            return
        }
        if (entity.isError) {
            listener.onGenericNotification(entity.errorMessage ?: "")
            listener.onEnableInput()
            listener.onEnableSaving()
            return
        }
        listener.onGenericNotification("")
        listener.onEnableInput()
        listener.onEnableSaving()
    }

}
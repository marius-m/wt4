package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.joda.time.DateTime
import java.time.*

class LogEditServiceImpl(
        private val logEditInteractor: LogEditInteractor,
        private val listener: LogEditService.Listener
) : LogEditService {

    override var serviceType: LogEditService.ServiceType = LogEditService.ServiceType.UPDATE
    override var entityInEdit: SimpleLog = SimpleLogBuilder(DateTime.now().millis)
            .setStart(DateTime.now().millis)
            .setEnd(DateTime.now().millis)
            .setTask("")
            .setComment("")
            .build()

    override fun updateDateTime(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime
    ) {
        val startInDateTime = LocalDateTime.of(startDate, startTime)
        val endInDateTime = LocalDateTime.of(endDate, endTime)
        try {
            entityInEdit = logEditInteractor.updateDateTime(entityInEdit, startInDateTime, endInDateTime)
            listener.onDurationChange(entityInEdit.prettyDuration)
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
            entityInEdit = logEditInteractor.updateTimeConvenience(
                    entityInEdit,
                    startInDateTime,
                    endInDateTime,
                    task,
                    comment
            )
            if (serviceType == LogEditService.ServiceType.UPDATE) {
                logEditInteractor.update(entityInEdit)
            } else {
                logEditInteractor.create(entityInEdit)
            }
            listener.onEntitySaveComplete()
        } catch(e: IllegalArgumentException) {
            listener.onEntitySaveFail(e)
        }
    }

    /**
     * Triggers according functions to show on screen
     */
    override fun redraw() {
        val start = Instant.ofEpochMilli(entityInEdit.start).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val end = Instant.ofEpochMilli(entityInEdit.end).atZone(ZoneId.systemDefault()).toLocalDateTime()
        listener.onDataChange(
                start,
                end,
                entityInEdit.task ?: "",
                entityInEdit.comment ?: ""
        )
        updateDateTime(
                start.toLocalDate(),
                start.toLocalTime(),
                end.toLocalDate(),
                end.toLocalTime()
        )
        printNotificationIfNeeded(entityInEdit)
    }

    /**
     * Generates a generic type of notification for the user
     */
    private fun printNotificationIfNeeded(entity: SimpleLog) {
        if (!entity.canEdit()) {
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
package lt.markmerkk.mvp

import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import org.joda.time.DateTime

class LogEditServiceImpl(
        private val logEditInteractor: LogEditInteractor,
        private val timeProvider: TimeProvider,
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
            start: DateTime,
            end: DateTime
    ) {
        try {
            entityInEdit = logEditInteractor.updateDateTime(entityInEdit, start, end)
            listener.onDurationChange(entityInEdit.prettyDuration)
            listener.onEnableSaving()
        } catch(e: IllegalArgumentException) {
            listener.onDurationChange("Invalid duration")
            listener.onDisableSaving()
        }
    }

    override fun saveEntity(
            start: DateTime,
            end: DateTime,
            task: String,
            comment: String
    ) {
        try {
            entityInEdit = logEditInteractor.updateTimeConvenience(
                    entityInEdit,
                    start,
                    end,
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
        val start = timeProvider.roundDateTime(entityInEdit.start)
        val end = timeProvider.roundDateTime(entityInEdit.end)
        listener.onDataChange(
                start,
                end,
                entityInEdit.task ?: "",
                entityInEdit.comment ?: ""
        )
        updateDateTime(start, end)
        printNotificationIfNeeded(entityInEdit)
    }

    override fun canEdit(): Boolean = entityInEdit.canEdit() || !entityInEdit.isError

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
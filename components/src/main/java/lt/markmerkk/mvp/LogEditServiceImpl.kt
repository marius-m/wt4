package lt.markmerkk.mvp

import lt.markmerkk.TicketStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.entities.TicketCode
import org.joda.time.DateTime

/**
 * Responsible for handling time change and input disable / enable
 * todo would benefit from refurbish, as remote logs can be updated, much of logic can be dropped
 */
class LogEditServiceImpl(
        private val logEditInteractor: LogEditInteractor,
        private val timeProvider: TimeProvider,
        private val ticketStorage: TicketStorage,
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
            ticketStorage.saveTicketAsUsedSync(timeProvider.preciseNow(), TicketCode.new(task))
            listener.onEntitySaveComplete(start, end)
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
        listener.onDataChange(start, end)
        updateDateTime(start, end)
        listener.onGenericNotification(entityInEdit.systemNote)
        printNotificationIfNeeded(entityInEdit)
    }

    /**
     * Generates a generic type of notification for the user
     */
    private fun printNotificationIfNeeded(entity: SimpleLog) {
        if (entity.isRemote) {
            listener.onDisableInput()
            listener.onDisableSaving()
            return
        }
        if (entity.systemNote.isNotEmpty()) {
            listener.onEnableInput()
            listener.onEnableSaving()
            return
        }
        listener.onEnableInput()
        listener.onEnableSaving()
    }

}
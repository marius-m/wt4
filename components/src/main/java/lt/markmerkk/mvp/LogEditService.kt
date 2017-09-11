package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Responsible for updating log and controlling the input view changes
 */
// todo : rename this to MVP pattern generics
interface LogEditService {

    var serviceType: ServiceType
    var entityInEdit: SimpleLog

    /**
     * Forces a redraw
     */
    fun redraw()

    /**
     * Updates current date time for the [entityInEdit]
     */
    fun updateDateTime(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime
    )

    /**
     * Update log with new input data
     * Depends on the [serviceType]. If CREATE, will create a new entity.
     */
    fun saveEntity(
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime,
            task: String,
            comment: String
    )

    interface Listener {
        /**
         * Refresh views with new log data
         */
        fun onDataChange(
                startDateTime: LocalDateTime,
                endDateTime: LocalDateTime,
                ticket: String,
                comment: String
        )

        /**
         * Notifies with new duration
         */
        fun onDurationChange(durationAsString: String)

        /**
         * Prints a notification just below the duration
         */
        fun onGenericNotification(notification: String)

        /**
         * Notifies when entity saved successfully
         */
        fun onEntitySaveComplete()

        /**
         * Notifies when entity save failed
         */
        fun onEntitySaveFail(error: Throwable)

        /**
         * Notifies the view to enable all input fields
         */
        fun onEnableInput()

        /**
         * Notifies the view to disable all input fields and saving trigger
         */
        fun onDisableInput()

        /**
         * Notifies the view to enable save button
         */
        fun onEnableSaving()

        /**
         * Notifies the view to disable save button
         */
        fun onDisableSaving()

    }

    enum class ServiceType {
        CREATE,
        UPDATE
    }

}
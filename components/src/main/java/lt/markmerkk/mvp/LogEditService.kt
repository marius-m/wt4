package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import org.joda.time.DateTime

/**
 * Responsible for updating log and controlling the input view changes
 */
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
            start: DateTime,
            end: DateTime
    )

    /**
     * Update log with new input data
     * Depends on the [serviceType]. If CREATE, will create a new entity.
     */
    fun saveEntity(
            start: DateTime,
            end: DateTime,
            task: String,
            comment: String
    )

    interface Listener {
        /**
         * Refresh views with new log data
         */
        fun onDataChange(
                start: DateTime,
                end: DateTime,
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
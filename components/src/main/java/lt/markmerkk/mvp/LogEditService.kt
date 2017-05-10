package lt.markmerkk.mvp

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Responsible for updating log and controlling the input view changes
 */
interface LogEditService {
    /**
     * Start of a life-cycle
     */
    fun onAttach()

    /**
     * End of life-cycle
     */
    fun onDetach()

    fun updateDateTime(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime
    )

    /**
     * Update log with new input data
     */
    fun saveEntity(
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
            ticket: String,
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
         * Notifies when entity saved successfully
         */
        fun onEntitySaveComplete()

        /**
         * Notifies the view to enable all input fields
         */
        fun onEnableInput()

        /**
         * Notifies the view to disable all input fields and saving trigger
         */
        fun onDisableInput()

    }

}
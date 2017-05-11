package lt.markmerkk.mvp

import java.time.LocalDate
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
            startDate: LocalDate,
            startTime: LocalTime,
            endDate: LocalDate,
            endTime: LocalTime
    )

    /**
     * Update log with new input data
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
         * Notifies when entity saved successfully
         */
        fun onEntitySaveComplete()

        /**
         * Notifies when entity saved successfully
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

}
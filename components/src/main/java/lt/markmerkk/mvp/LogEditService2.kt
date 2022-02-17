package lt.markmerkk.mvp

import lt.markmerkk.entities.TimeGap

/**
 * Responsible for updating log and controlling the input view changes
 */
interface LogEditService2 {
    var serviceType: ServiceType

    fun bindLogByLocalId(localId: Long)

    /**
     * Forces a redraw
     */
    fun redraw()

    /**
     * Updates current date time for the [entityInEdit]
     */
    fun updateDateTime(timeGap: TimeGap)

    /**
     * Update log with new input data
     * Depends on the [serviceType]. If CREATE, will create a new entity.
     */
    fun saveEntity(
            timeGap: TimeGap,
            task: String,
            comment: String
    )

    interface Listener {
        /**
         * Refresh views with new log data
         */
        fun showDataTimeChange(timeGap: TimeGap)

        /**
         * Refresh views with new log data
         */
        fun showDuration(durationAsString: String)

        /**
         * Enables / disables input field editing
         */
        fun lockEdit(isEnabled: Boolean)

        fun showSuccess()
    }

    enum class ServiceType {
        CREATE,
        UPDATE
    }

}
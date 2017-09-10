package lt.markmerkk.ui_2

import javafx.geometry.Pos
import lt.markmerkk.entities.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2017-09-09
 */
interface LogStatusCallback {
    /**
     * Show [SimpleLog] info with a local id of the log.
     * Null value counts as the [SimpleLog] was not found, and it will hide the view.
     */
    fun showLogWithId(logId: Long?)

    /**
     * Suggest view gravity based on which day of the week it is.
     * This is needed, whenever selecting an appointment in [Agenda] would not overlap the position
     * of the appointment itself.
     */
    fun suggestGravityByLogWeekDay(simpleLog: SimpleLog): Pos
}
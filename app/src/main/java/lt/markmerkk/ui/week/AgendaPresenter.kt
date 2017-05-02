package lt.markmerkk.ui.week

import lt.markmerkk.entities.SimpleLog

/**
 * Presents agenda loader for MVP
 */
interface AgendaPresenter {
    /**
     * Start of lifecycle
     */
    fun onAttach()

    /**
     * End of lifecycl
     */
    fun onDetatch()

    /**
     * Triggers to reload a view with logs
     */
    fun reloadView(logs: List<SimpleLog>)
}
package lt.markmerkk.ui.week

import lt.markmerkk.storage2.SimpleLog

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface AgendaPresenter {
    fun onAttach()
    fun onDetatch()
    fun reloadView(logs: List<SimpleLog>)
}
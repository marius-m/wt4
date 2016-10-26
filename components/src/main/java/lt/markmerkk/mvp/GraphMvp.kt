package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
interface GraphMvp {

    interface LogInteractor {
        fun loadLogs(
                fromMillis: Long,
                toMillis: Long
        ) : Observable<List<SimpleLog>>
    }

    interface View {
        fun showProgress()
        fun hideProgress()

        fun showGraph(drawer: GraphDrawer<*>)
        fun showErrorGraph(message: String)
    }

    interface Presenter {
        fun onAttach()
        fun onDetach()

        fun loadGraph()
    }
}
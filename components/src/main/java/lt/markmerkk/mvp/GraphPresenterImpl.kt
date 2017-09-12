package lt.markmerkk.mvp

import javafx.scene.layout.Region
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer
import org.joda.time.DateTime
import org.joda.time.LocalTime
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
class GraphPresenterImpl(
        private val view: GraphMvp.View,
        private val logInteractor: GraphMvp.LogInteractor,
        private val graphDrawers: List<GraphDrawer<*>>,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : GraphMvp.Presenter {

    private var subscription: Subscription? = null
    var selectGraphIndex = 0

    override fun onAttach() {
        view.hideRefreshButton()
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun loadGraph(from: Long, to: Long) {
        subscription?.unsubscribe()
        val fromDayStart = DateTime(from).withTimeAtStartOfDay()
        val toDayEnd = DateTime(to).withTime(LocalTime.MIDNIGHT.minusSeconds(1))
        subscription = logInteractor.loadLogs(fromDayStart.millis, toDayEnd.millis)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnTerminate { view.hideProgress() }
                .subscribe({
                    handleSuccess(it)
                }, {
                    view.showErrorGraph("Error loading graph")
                    it.printStackTrace()
                })
    }

    override fun refresh() {
        if (graphDrawers.isEmpty()) return
        if (selectGraphIndex >= graphDrawers.size || selectGraphIndex < 0) return
        graphDrawers.get(selectGraphIndex).refresh()
    }

    fun handleSuccess(data: List<SimpleLog>) {
        if (graphDrawers.size == 0) {
            view.showErrorGraph("Error rendering graph")
            return
        }
        if (selectGraphIndex >= graphDrawers.size || selectGraphIndex < 0) {
            view.showErrorGraph("Invalid graph selection")
            return
        }
        val graph = graphDrawers.get(selectGraphIndex)
        if (graph.isRefreshable) {
            view.showRefreshButton()
        } else {
            view.hideRefreshButton()
        }
        graph.populateData(data as List<Nothing>)
        view.showGraph(graph)
    }
}
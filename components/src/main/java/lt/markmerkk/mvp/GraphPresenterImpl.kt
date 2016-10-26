package lt.markmerkk.mvp

import javafx.scene.layout.Region
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.interactors.GraphDrawer
import org.joda.time.DateTime
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
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun loadGraph(from: Long, to: Long) {
        subscription?.unsubscribe()
        val tomorrow = DateTime(to).plusDays(1).millis
        logInteractor.loadLogs(from, tomorrow)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .subscribe({
                    handleSuccess(it)
                }, {
                    view.showErrorGraph("Error loading graph")
                    it.printStackTrace()
                })
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
        graph.populateData(data as List<Nothing>) // Maybe improve this part
        view.showGraph(graph)
    }
}
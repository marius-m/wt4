package lt.markmerkk.mvp

import javafx.scene.layout.Region
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

    override fun loadGraph() {
        subscription?.unsubscribe()
        logInteractor.loadLogs(DateTime().minusMonths(1).millis, DateTime().plusMonths(1).millis)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showProgress() }
                .doOnUnsubscribe { view.hideProgress() }
                .subscribe({
                    handleSuccess()
                }, {
                    view.showErrorGraph("Error loading data for graph")
                })
    }


    fun handleSuccess() {
        if (graphDrawers.size == 0) {
            view.showErrorGraph("Error rendering graph")
            return
        }
        if (selectGraphIndex >= graphDrawers.size || selectGraphIndex < 0) {
            view.showErrorGraph("Invalid graph selection")
            return
        }
        view.showGraph(graphDrawers.get(selectGraphIndex))
    }
}
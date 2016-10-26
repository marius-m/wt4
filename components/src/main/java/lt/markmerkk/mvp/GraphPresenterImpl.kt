package lt.markmerkk.mvp

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
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : GraphMvp.Presenter {

    var subscription: Subscription? = null

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
                    view.showGraph()
                }, {
                    view.showErrorGraph(it.message!!)
                })
    }
}
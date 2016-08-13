package lt.markmerkk.mvp

import lt.markmerkk.interactors.IssueSearchInteractor
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
class IssueSearchPresenterImpl(
        private val view: IssueSearchMvp.View,
        private val issueSearchInteractor: IssueSearchInteractor,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) : IssueSearchMvp.Presenter {

    private var searchSubscription: Subscription? = null
    private val subscriptions = mutableListOf<Subscription>()
    private val searchSubject = PublishSubject.create<String>()

    override fun onAttach() {
        searchSubject
                .debounce(500, TimeUnit.MILLISECONDS, ioScheduler)
                .subscribe({
                    searchSubscription?.unsubscribe()
                    searchSubscription = Observable.just(it)
                            .subscribeOn(ioScheduler)
                            .flatMap {
                                if (it.isNullOrEmpty() || it.length <= 2) {
                                    issueSearchInteractor.allIssues()
                                } else {
                                    issueSearchInteractor.searchIssues(it)
                                }
                            }
                            .flatMap { Observable.from(it) }
                            .take(100)
                            .toList()
                            .observeOn(uiScheduler)
                            .subscribe({
                                if (it.size > 0) {
                                    view.showIssues(it)
                                } else {
                                    view.hideIssues()
                                }
                            }, {
                                view.hideIssues()
                            })
                }).apply { subscriptions += this }
        recountIssues()
    }

    override fun onDetach() {
        searchSubject.onCompleted()
        searchSubscription?.unsubscribe()
        subscriptions.forEach { it.unsubscribe() }
    }

    override fun search(issuePhrase: String) {
        searchSubject.onNext(issuePhrase)
    }

    override fun recountIssues() {
        issueSearchInteractor.issueCount()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    view.showTotalIssueCount(it)
                }, {
                    view.showTotalIssueCount(0)
                }).apply { subscriptions += this }
    }

    companion object {
        val logger = LoggerFactory.getLogger(IssueSearchPresenterImpl::class.java)!!
    }

}
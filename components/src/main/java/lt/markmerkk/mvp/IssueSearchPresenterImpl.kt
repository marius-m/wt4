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

    private var innerSubscription: Subscription? = null
    private var subscription: Subscription? = null
    private val searchSubject = PublishSubject.create<String>()

    override fun onAttach() {
        subscription = searchSubject
                .debounce(500, TimeUnit.MILLISECONDS, ioScheduler)
                .subscribe({
                    innerSubscription?.unsubscribe()
                    innerSubscription = Observable.just(it)
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
                })
    }

    override fun onDetach() {
        searchSubject.onCompleted()
        innerSubscription?.unsubscribe()
        subscription?.unsubscribe()
    }

    override fun search(issuePhrase: String) {
        searchSubject.onNext(issuePhrase)
    }

    companion object {
        val logger = LoggerFactory.getLogger(IssueSearchPresenterImpl::class.java)!!
    }

}
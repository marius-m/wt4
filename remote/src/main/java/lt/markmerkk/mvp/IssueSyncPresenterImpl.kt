package lt.markmerkk.mvp

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraFilterIssue
import lt.markmerkk.JiraInteractor
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.merger.RemoteMergeToolsProvider
import net.rcarz.jiraclient.Issue
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
class IssueSyncPresenterImpl(
        private val view: IssueSyncMvp.View,
        private val remoteMergeToolsProvider: RemoteMergeToolsProvider,
        private val jiraInteractor: JiraInteractor,
        private val dataStorage: IDataStorage<LocalIssue>,
        private val uiScheduler: Scheduler
) : IssueSyncMvp.Presenter {

    var subscription: Subscription? = null

    override fun onAttach() {
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun sync() {
        val filter = JiraFilterIssue()
        subscription = issueCacheObservable(filter)
                .observeOn(uiScheduler)
                .subscribe({
                    logger.info("Issue sync success!")
                }, {
                    logger.error("Error syncing issues: ${it.message}")
                    dataStorage.notifyDataChange()
                }, {
                    dataStorage.notifyDataChange()
                })
    }

    fun issueCacheObservable(filter: JiraFilter<Issue>): Observable<List<Issue>> {
        return jiraInteractor.jiraIssues()
                .flatMap { Observable.from(it) }
                .flatMap {
                    val merger = remoteMergeToolsProvider.issuePullMerger(it, filter)
                    Observable.fromCallable(merger)
                }
                .toList()
    }

    companion object {
        val logger = LoggerFactory.getLogger(IssueSyncPresenterImpl::class.java)!!
    }

}
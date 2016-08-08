package lt.markmerkk

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.IDataStorage
import net.rcarz.jiraclient.Issue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
class JiraInteractorImpl(
        val jiraClientProvider: JiraClientProvider,
        val localStorage: IDataStorage<SimpleLog>,
        val jiraSearchSubscriber: JiraSearchSubscriber,
        val jiraWorklogSubscriber: JiraWorklogSubscriber
) : JiraInteractor {

    //region Observables

    override fun jiraRemoteWorks(start: Long, end: Long): Observable<List<JiraWork>> {
        return Observable.defer { Observable.just(jiraClientProvider.client()) }
                .flatMap { jiraSearchSubscriber.searchResultObservable(start, end) }
                .flatMap { jiraWorklogSubscriber.worklogResultObservable(it) }
                .filter { it.valid() }
                .reduce(
                        mutableListOf<JiraWork>(),
                        { accumulator, next ->
                            accumulator.add(next)
                            accumulator
                        }
                )
                .map { it.toList() }
    }

    override fun jiraLocalWorks(): Observable<List<SimpleLog>> {
        return Observable.from(localStorage.dataAsList)
                .toList()
    }

    override fun jiraIssues(): Observable<Issue> { // todo : incomplete
        return Observable.empty<Issue>()
    }

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
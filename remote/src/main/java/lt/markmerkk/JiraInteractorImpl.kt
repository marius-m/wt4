package lt.markmerkk

import lt.markmerkk.JiraWork
import net.rcarz.jiraclient.Issue
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
class JiraInteractorImpl(
        val jiraClientProvider: JiraClientProvider,
        val jiraSearchSubsciber: JiraSearchSubsciber,
        val jiraWorklogSubscriber: JiraWorklogSubscriber
) : JiraInteractor {

    //region Observables

    override fun jiraWorks(start: Long, end: Long): Observable<List<JiraWork>> {
        return jiraClientProvider.clientObservable()
                .flatMap { jiraSearchSubsciber.searchResultObservable(start, end) }
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

    override fun jiraIssues(): Observable<Issue> { // todo : incomplete
        return Observable.empty<Issue>()
    }

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
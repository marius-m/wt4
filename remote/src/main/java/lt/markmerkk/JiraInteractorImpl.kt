package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
open class JiraInteractorImpl(
        val jiraClientProvider: JiraClientProvider,
        val jiraSearchSubsciber: JiraSearchSubsciber,
        val jiraWorklogSubscriber: JiraWorklogSubscriber,
        val ioScheduler: Scheduler,
        val uiScheduler: Scheduler
) : JiraInteractor {

    //region Observables

    fun jiraWorks(start: DateTime, end: DateTime): Observable<List<JiraWork>> {
        return jiraClientProvider.clientObservable()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
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

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
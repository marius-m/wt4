package lt.markmerkk

import lt.markmerkk.entities.*
import lt.markmerkk.tickets.JiraSearchSubscriber
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler

class JiraInteractorImpl(
        val jiraClientProvider: JiraClientProvider,
        val logStorage: IDataStorage<SimpleLog>,
        val jiraSearchSubscriber: JiraSearchSubscriber,
        val jiraWorklogSubscriber: JiraWorklogSubscriber,
        val ioScheduler: Scheduler
) : JiraInteractor {

    //region Observables

    // fixme: Should not provide schedulers with observables
    override fun jiraRemoteWorks(start: Long, end: Long): Observable<List<JiraWork>> {
        return Observable.defer { jiraClientProvider.clientStream().toObservable() }
                .subscribeOn(ioScheduler)
                .flatMap { jiraSearchSubscriber.workedIssuesObservable(start, end) }
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

    // fixme: Should not provide schedulers with observables
    override fun jiraLocalWorks(): Observable<List<SimpleLog>> {
        return Observable.defer { jiraClientProvider.clientStream().toObservable() }
                .subscribeOn(ioScheduler)
                .flatMap { Observable.from(logStorage.data) }
                .toList()
    }

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
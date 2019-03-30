package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.tickets.JiraSearchSubscriber
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

class JiraInteractorImpl(
        val jiraClientProvider: JiraClientProvider,
        val logStorage: IDataStorage<SimpleLog>,
        val jiraSearchSubscriber: JiraSearchSubscriber,
        val jiraWorklogSubscriber: JiraWorklogSubscriber
) : JiraInteractor {

    //region Observables

    override fun jiraRemoteWorks(start: Long, end: Long): Observable<List<JiraWork>> {
        return Observable.defer { jiraClientProvider.clientStream().toObservable() }
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

    override fun jiraLocalWorks(): Observable<List<SimpleLog>> {
        return Observable.defer { jiraClientProvider.clientStream().toObservable() }
                .flatMap { Observable.from(logStorage.data) }
                .toList()
    }

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
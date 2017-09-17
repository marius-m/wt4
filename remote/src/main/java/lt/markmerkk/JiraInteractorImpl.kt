package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.RemoteEntity
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.Issue
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
        val logStorage: IDataStorage<SimpleLog>,
        val issueStorage: IDataStorage<LocalIssue>,
        val jiraSearchSubscriber: JiraSearchSubscriber,
        val jiraWorklogSubscriber: JiraWorklogSubscriber,
        val ioScheduler: Scheduler
) : JiraInteractor {

    //region Observables

    // fixme: Should not provide schedulers with observables
    override fun jiraRemoteWorks(start: Long, end: Long): Observable<List<JiraWork>> {
        return Observable.defer { Observable.just(jiraClientProvider.client()) }
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
        return Observable.defer { Observable.just(jiraClientProvider.client()) }
                .subscribeOn(ioScheduler)
                .flatMap { Observable.from(logStorage.data) }
                .toList()
    }

    // fixme: Should not provide schedulers with observables
    override fun jiraIssues(): Observable<List<Issue>> {
        return Observable.defer { Observable.just(jiraClientProvider.client()) }
                .subscribeOn(ioScheduler)
                .flatMap { jiraSearchSubscriber.userIssuesObservable() }
                .flatMap { Observable.from(it.issues) }
                .toList()
    }

    // fixme: Should not provide schedulers with observables
    override fun jiraLocalIssuesOld(startSync: Long): Observable<List<LocalIssue>> {
        return Observable.defer {
            Observable.from(
                    issueStorage.customQuery(
                            String.format(
                                    "%s < %d",
                                    RemoteEntity.KEY_DOWNLOAD_MILLIS,
                                    startSync
                            )
                    ))
        }.subscribeOn(ioScheduler)
                .filter { startSync > it.download_millis } // assuring old items, not necessary
                .subscribeOn(ioScheduler)
                .toList()
    }

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
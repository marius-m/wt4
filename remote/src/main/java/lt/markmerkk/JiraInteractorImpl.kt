package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Single

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
open class JiraInteractorImpl(
        val host: String,
        val username: String,
        val password: String
) : JiraInteractor {

    var jiraClient: JiraClient? = null

    val clientObservable: Observable<JiraClient> // Cache client
        get() {
            if (jiraClient == null) {
                return Observable.create(JiraConnector(host, username, password))
            }
            return Observable.just(jiraClient)
        }

    //region Observables

    open fun clientObservable(): Observable<JiraClient> {
        return clientObservable.flatMap {
            jiraClient = it
            Observable.just(it)
        }
    }

    fun searchJqlForWorklog(start: DateTime, end: DateTime): Observable<List<JiraWork>> {
        return clientObservable.flatMap {
            Observable.create<Issue.SearchResult>(
                    JiraSearchJQL(it, jqlForWorkIssuesFromDateObservable(start, end))
            )
        }
                .filter { it.issues.size != 0 }
                .flatMap { Observable.from(it.issues) }
                .map { it.key }
                .flatMap { Observable.just(jiraClient?.getIssue(it)) }
                .flatMap({ Observable.just(it?.allWorkLogs) },
                        { issue, worklogs ->
                            JiraWork(issue, worklogs)
                        }
                )
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

    //region Convenience

    fun jqlForWorkIssuesFromDateObservable(
            start: DateTime,
            end: DateTime
    ): String {
        val startFormat = JiraSearchJQL.dateFormat.print(start.millis)
        val endFormat = JiraSearchJQL.dateFormat.print(end.millis)
        return "key in workedIssues(\"$startFormat\", \"$endFormat\", \"$username\")"
    }

    //endregion

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
package lt.markmerkk

import javafx.util.Pair
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler

/**
 * @author mariusmerkevicius
 * @since 2016-07-03
 */
class JiraObservables2(
        val host: String,
        val username: String,
        val password: String,
        val ioScheduler: Scheduler,
        val uiScheduler: Scheduler
) {
    var jiraClient: JiraClient? = null
        private set

    private val clientObservable: Observable<JiraClient>
        get() {
            if (jiraClient == null) {
                return Observable.create(JiraConnector(host, username, password))
            }
            return Observable.just(jiraClient)
        }

    fun clientObservable(): Observable<JiraClient> {
        return clientObservable.flatMap {
            jiraClient = it
            Observable.just(it)
        }
    }

    fun searchJqlForWorklog(start: DateTime, end:DateTime, client: JiraClient): Observable<Any> {
        return Observable.create<Issue.SearchResult>(JiraSearchJQL(client, jqlForWorkIssuesFromDateObservable(start, end)))
                .filter { it.issues.size != 0 }
                .flatMap { Observable.from(it.issues) }
                .map { it.key }
                .flatMap { Observable.just(client.getIssue(it)) }
                .flatMap({ Observable.just(it.allWorkLogs) },
                        { issue, worklogs -> Pair<Issue, List<WorkLog>>(issue, worklogs) })
    }

    fun jqlForWorkIssuesFromDateObservable(
            start: DateTime,
            end: DateTime
    ): String {
        val startFormat = JiraSearchJQL.dateFormat.print(start.millis)
        val endFormat = JiraSearchJQL.dateFormat.print(end.millis)
        return "key in workedIssues(\"$startFormat\", \"$endFormat\", \"$username\")"
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger("JiraObservables2")
    }

}
package lt.markmerkk

import javafx.util.Pair
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
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

    fun remoteWorklogs(start: DateTime, end: DateTime, client: JiraClient): Observable<Pair<Issue, List<WorkLog>>> {
        return Observable.empty()
    }

    fun searchJqlForWorklog(jql: String, client: JiraClient): Observable<Issue.SearchResult> {
        return Observable.create<Issue.SearchResult>(JiraSearchJQL(client, jql))
    }

    fun jqlForWorkIssuesFromDateObservable(
            start: DateTime,
            end: DateTime
    ): String {
        val startFormat = JiraSearchJQL.dateFormat.print(start.millis)
        val endFormat = JiraSearchJQL.dateFormat.print(end.millis)
        return "key in workedIssues(\"$startFormat\", \"$endFormat\", \"$username\")"
    }

}
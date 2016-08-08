package lt.markmerkk

import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import org.joda.time.format.DateTimeFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber

/**
 * Created by mariusmerkevicius on 1/23/16.
 */
class JiraSearchSubscriberImpl : JiraSearchSubscriber, Observable.OnSubscribe<Issue.SearchResult> {

    val jiraClientProvider: JiraClientProvider
    val searchFields: String = "*all"
    var jiraClient: JiraClient? = null
    var jql: String? = ""

    constructor(
            jiraClientProvider: JiraClientProvider
    ) {
        this.jiraClientProvider = jiraClientProvider
    }

    private constructor(
            jiraClientProvider: JiraClientProvider,
            jql: String,
            jiraClient: JiraClient
    ) {
        this.jiraClientProvider = jiraClientProvider
        this.jql = jql
        this.jiraClient = jiraClient
    }


    override fun call(subscriber: Subscriber<in Issue.SearchResult>) {
        try {
            logger.info("Doing search: " + jql)
            var batchCurrent = 0
            val batchSize = 50
            var batchTotal = 0
            do {
                if (subscriber.isUnsubscribed)
                    break

                val sr = jiraClient?.searchIssues(jql, searchFields, batchSize, batchCurrent)
                if (sr == null) throw IllegalStateException("Search result is empty")
                if (sr.issues == null) throw IllegalStateException("Search result is empty")
                if (sr.issues.size == 0) throw IllegalStateException("Search result is empty")
                logger.info("Found issues " + sr.issues.size + " that have been worked on.")
                subscriber.onNext(sr)

                batchCurrent += sr.max
                batchTotal = sr.total
            } while (batchCurrent < batchTotal)
            subscriber.onCompleted()
        } catch (e: IllegalStateException) {
            logger.info("Jira search error: ${e.message}")
            subscriber.onCompleted()
        } catch (e: JiraException) {
            logger.error("Jira error: $e")
            subscriber.onCompleted()
        }

    }

    override fun searchResultObservable(start: Long, end: Long): Observable<Issue.SearchResult> {
        return Observable.just(jiraClientProvider.client())
                .flatMap {
                    Observable.create<Issue.SearchResult>(
                            JiraSearchSubscriberImpl(
                                    jiraClientProvider = jiraClientProvider,
                                    jql = jqlForWorkIssuesFromDateObservable(start, end, it),
                                    jiraClient = it
                            )
                    )
                }
    }

    fun jqlForWorkIssuesFromDateObservable(
            start: Long,
            end: Long,
            client: JiraClient
    ): String {
        val startFormat = JiraSearchSubscriberImpl.dateFormat.print(start)
        val endFormat = JiraSearchSubscriberImpl.dateFormat.print(end)
        return "key in workedIssues(\"$startFormat\", \"$endFormat\", \"${client.self}\")"
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger("JiraSearchJQL")
        val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
        val DEFAULT_JQL_WORKLOG_TEMPLATE = "key in workedIssues(\"%s\", \"%s\", \"%s\")"
        val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved)) AND (assignee = currentUser() OR reporter = currentUser())"
    }

}

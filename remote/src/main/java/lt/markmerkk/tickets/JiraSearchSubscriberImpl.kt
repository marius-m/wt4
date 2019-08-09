package lt.markmerkk.tickets

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Tags
import lt.markmerkk.UserSettings
import lt.markmerkk.utils.LogFormatters
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber

class JiraSearchSubscriberImpl(
        private val jiraClientProvider: JiraClientProvider,
        private val userSettings: UserSettings,
        private val searchFields: String = "*all",
        private var jql: String = ""
) : JiraSearchSubscriber, Observable.OnSubscribe<Issue.SearchResult> {

    override fun call(subscriber: Subscriber<in Issue.SearchResult>) {
        try {
            if (jql.isEmpty()) throw IllegalArgumentException("JQL is empty!")
            logger.info("Doing search: " + jql)
            var startAt = 0
            val max = 50
            var total: Int
            do {
                if (subscriber.isUnsubscribed)
                    break

                val sr = jiraClientProvider.clientStream()
                        .toBlocking() // todo should not be done like this
                        .value()
                        .searchIssues(jql, searchFields, max, startAt)
                if (sr == null) throw IllegalStateException("result is empty")
                if (sr.issues == null) throw IllegalStateException("result is empty")
                if (sr.issues.size == 0) throw IllegalStateException("result is empty")
                logger.info("Found ${sr.issues.size} issues.")
                subscriber.onNext(sr)

                startAt += sr.max
                total = sr.total
            } while (startAt < total)
            subscriber.onCompleted()
        } catch (e: IllegalStateException) {
            logger.info("Jira search ${e.message}")
            subscriber.onCompleted()
        } catch (e: IllegalArgumentException) {
            logger.error("Jira search error: ${e.message}")
            subscriber.onError(e)
        } catch (e: JiraException) {
            logger.error("Jira error: ${e.message}")
            subscriber.onError(e)
        }
    }

    override fun workedIssuesObservable(start: Long, end: Long): Observable<Issue.SearchResult> {
        return Observable.create(
                JiraSearchSubscriberImpl(
                        jiraClientProvider = jiraClientProvider,
                        userSettings = userSettings,
                        jql = jqlForWorkIssuesFromDateObservable(start, end),
                        searchFields = "*all"
                )
        )
    }

    override fun userIssuesObservable(): Observable<Issue.SearchResult> {
        return Observable.create(
                JiraSearchSubscriberImpl(
                        jiraClientProvider = jiraClientProvider,
                        userSettings = userSettings,
                        jql = userSettings.issueJql,
                        searchFields = "summary,project,created,updated,parent,issuetype"
                )
        )
    }

    fun jqlForWorkIssuesFromDateObservable(
            start: Long,
            end: Long
    ): String {
        val startFormat = LogFormatters.shortFormatDate.print(start)
        val endFormat = LogFormatters.shortFormatDate.print(end)
        return "(worklogDate >= \"$startFormat\" && worklogDate <= \"$endFormat\")" +
                " AND " +
                "(assignee = currentUser() OR reporter = currentUser() OR watcher = currentUser())"
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}

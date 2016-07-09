package lt.markmerkk

import com.google.common.base.Strings
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber

/**
 * Created by mariusmerkevicius on 1/23/16.
 */
class JiraSearchJQL(
        val client: JiraClient,
        val jql: String,
        val searchFields: String = "*all"
) : Observable.OnSubscribe<Issue.SearchResult> {

    override fun call(subscriber: Subscriber<in Issue.SearchResult>) {
        try {
            logger.info("Doing search: " + jql)
            var batchCurrent = 0
            val batchSize = 50
            var batchTotal = 0
            do {
                if (subscriber.isUnsubscribed)
                    break

                val sr = client.searchIssues(jql, searchFields, batchSize, batchCurrent)
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
            logger.error("Jira search error: ${e.message}")
            subscriber.onCompleted()
        } catch (e: JiraException) {
            logger.error("Jira error: $e")
            subscriber.onCompleted()
        }

    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger("JiraSearchJQL")
        val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
        val DEFAULT_JQL_WORKLOG_TEMPLATE = "key in workedIssues(\"%s\", \"%s\", \"%s\")"
        val DEFAULT_JQL_USER_ISSUES = "(status not in (closed, resolved)) AND (assignee = currentUser() OR reporter = currentUser())"
    }

}

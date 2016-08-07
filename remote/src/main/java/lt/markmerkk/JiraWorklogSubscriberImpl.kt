package lt.markmerkk

import lt.markmerkk.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 * Responsible for pulling worklogs from jira
 */
class JiraWorklogSubscriberImpl : JiraWorklogSubscriber, Observable.OnSubscribe<JiraWork> {
    val jiraClientProvider: JiraClientProvider
    var searchResult: Issue.SearchResult? = null
    var jiraClient: JiraClient? = null

    constructor(
            jiraClientProvider: JiraClientProvider
    ) {
        this.jiraClientProvider = jiraClientProvider
    }

    private constructor(
            jiraClientProvider: JiraClientProvider,
            searchResult: Issue.SearchResult,
            jiraClient: JiraClient
    ) {
        this.jiraClientProvider = jiraClientProvider
        this.searchResult = searchResult
        this.jiraClient = jiraClient
    }

    override fun call(t: Subscriber<in JiraWork>) {
        try {
            val searchResult = this.searchResult ?: throw IllegalStateException("No issues")
            if (searchResult.issues == null) throw IllegalStateException("No issues")
            searchResult.issues.forEach {
                val issue = jiraClient?.getIssue(it.key)
                var worklogs = emptyList<WorkLog>()
                if (issue != null) {
                    worklogs = issue.allWorkLogs
                }
                t.onNext(JiraWork(issue, worklogs))
            }
            t.onCompleted()
        } catch(e: IllegalStateException) {
            logger.info("Illegal state: ${e.message}")
            t.onCompleted()
        } catch(e: JiraException) {
            logger.error("Jira error: $e")
            t.onCompleted()
        }
    }

    override fun worklogResultObservable(searchResult: Issue.SearchResult): Observable<JiraWork> {
        return jiraClientProvider.clientObservable()
                .flatMap { Observable.create(JiraWorklogSubscriberImpl(jiraClientProvider, searchResult, it)) }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(JiraException::class.java)
    }

}
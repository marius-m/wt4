package lt.markmerkk.worklogs

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Tags
import lt.markmerkk.exceptions.AuthException
import net.rcarz.jiraclient.JiraException
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.functions.Action1

internal class JiraWorklogEmitter(
        private val jiraClientProvider: JiraClientProvider,
        private val jql: String,
        private val searchFields: String,
        private val start: LocalDate,
        private val end: LocalDate
) : Action1<Emitter<IssueWorklogPair>> {

    override fun call(emitter: Emitter<IssueWorklogPair>) {
        try {
            val jiraClient = jiraClientProvider.client()
            if (jql.isEmpty()) throw IllegalArgumentException("JQL is empty!")
            logger.info("Searching for worklogs using JQL: $jql")
            var startAt = 0
            val max = 50
            var total: Int
            do {
                val sr = jiraClient
                        .searchIssues(jql, searchFields, max, startAt)
                if (sr.issues == null) throw IllegalStateException("result is empty")
                if (sr.issues.size == 0) throw IllegalStateException("result is empty")
                logger.info("Found ${sr.issues.size} issues.")
                sr.issues
                        .map { IssueWorklogPair(it, it.allWorkLogs) }
                        .forEach { emitter.onNext(it) }
                startAt += sr.max
                total = sr.total
            } while (startAt < total)
            emitter.onCompleted()
        } catch (e: IllegalStateException) {
            logger.info("Jira search ${e.message}")
            emitter.onCompleted()
        } catch (e: IllegalArgumentException) {
            logger.error("Jira search error: ${e.message}")
            emitter.onError(e)
        } catch (e: JiraException) {
            logger.error("Jira error: ${e.message}")
            emitter.onError(e)
        } catch (e: AuthException) {
            logger.error("Jira error: ${e.message}")
            emitter.onError(e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }
}

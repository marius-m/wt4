package lt.markmerkk.tickets

import lt.markmerkk.clientextension.JiraClientExt
import lt.markmerkk.Tags
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraException
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.functions.Action1

class JiraTicketEmitter(
    private val jiraClient: JiraClientExt,
    private val searchFields: String = "*all",
    private val jql: String = ""
) : Action1<Emitter<List<Issue>>> {

    override fun call(emitter: Emitter<List<Issue>>) {
        try {
            if (jql.isEmpty()) throw IllegalArgumentException("JQL is empty!")
            logger.info("Doing search: $jql")
            var startAt = 0
            val max = 50
            var total: Int
            do {
                val sr = jiraClient
                        .searchIssues(jql, searchFields, max, startAt)
                if (sr == null) throw IllegalStateException("result is empty")
                if (sr.issues == null) throw IllegalStateException("result is empty")
                if (sr.issues.size == 0) throw IllegalStateException("result is empty")
                logger.info("Found ${sr.issues.size} issues.")
                emitter.onNext(sr.issues)

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
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}

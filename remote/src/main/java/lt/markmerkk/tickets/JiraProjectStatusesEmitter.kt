package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.entities.TicketStatus
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.Project
import net.rcarz.jiraclient.Status
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.functions.Action1

class JiraProjectStatusesEmitter(
        private val jiraClient: JiraClient
) : Action1<Emitter<List<Status>>> {

    override fun call(emitter: Emitter<List<Status>>) {
        try {
            val projects = jiraClient.projects
            logger.info("Found ${projects.size} projects.")
            val projectStatuses = projects
                    .flatMap { jiraClient.getProjectStatuses(it.key) }
                    .map { it.name to it } // status cannot be compared, so using map to filter
                    .toMap()
            logger.info("Found ${projectStatuses.size} project statuses.")
            emitter.onNext(projectStatuses.values.toList())
            emitter.onCompleted()
        } catch (e: IllegalStateException) {
            logger.info("Jira project statuses ${e.message}")
            emitter.onCompleted()
        } catch (e: IllegalArgumentException) {
            logger.error("Jira project statuses error: ${e.message}")
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

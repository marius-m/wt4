package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.clientextension.JiraClientExt
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.Project
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.functions.Action1

class JiraProjectStatusesEmitter(
        private val jiraClient: JiraClientExt,
) : Action1<Emitter<Set<String>>> {

    override fun call(emitter: Emitter<Set<String>>) {
        try {
            val projects: List<Project> = jiraClient.projects
            logger.info("Found ${projects.size} projects.")
            val projectStatuses: Set<String> = projects
                .fold(mutableSetOf()) { acc, project ->
                    val statusesPerProject: Set<String> = jiraClient.fetchProjectStatuses(project.key)
                    acc.addAll(statusesPerProject)
                    acc
                }
            logger.info("Found ${projectStatuses.size} statuses per ${projects.size} projects.")
            emitter.onNext(projectStatuses.toSet())
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

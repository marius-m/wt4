package lt.markmerkk.tickets

import lt.markmerkk.Tags
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable

class JiraTicketSearch {

    fun searchIssues(
            jiraClient: JiraClient,
            jql: String
    ): Observable<List<Issue>> {
        return Observable.create(
                JiraTicketEmitter(
                        jiraClient = jiraClient,
                        jql = jql,
                        searchFields = "summary,project,created,updated,parent,issuetype"
                ),
                Emitter.BackpressureMode.BUFFER
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}

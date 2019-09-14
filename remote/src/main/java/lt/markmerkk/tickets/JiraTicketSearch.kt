package lt.markmerkk.tickets

import lt.markmerkk.Tags
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable

class JiraTicketSearch {

    fun searchIssues(
            now: DateTime,
            jiraClient: JiraClient,
            jql: String
    ): Observable<Ticket> {
        return Observable.create(
                JiraTicketEmitter(
                        jiraClient = jiraClient,
                        jql = jql,
                        searchFields = "summary,project,created,updated,parent,issuetype"
                ),
                Emitter.BackpressureMode.BUFFER
        ).flatMap {
            Observable.from(it)
        }.map {
            Ticket.fromRemoteData(
                    code = it.key,
                    description = it.summary,
                    remoteData = RemoteData.fromRemote(
                            fetchTime = now.millis,
                            url = it.url
                    )
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
    }

}

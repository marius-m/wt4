package lt.markmerkk.tickets

import lt.markmerkk.Const
import lt.markmerkk.Tags
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.entities.TicketStatus
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable
import rx.Single

class JiraTicketSearch {

    fun projectStatuses(
            now: DateTime,
            jiraClient: JiraClient
    ): Single<List<TicketStatus>> {
        return Observable
                .create(JiraProjectStatusesEmitter(jiraClient), Emitter.BackpressureMode.BUFFER)
                .toSingle()
                .map { statuses ->
                    statuses.map { TicketStatus(it.name) }
                }
    }

    fun searchIssues(
            now: DateTime,
            jiraClient: JiraClient,
            jql: String
    ): Observable<Ticket> {
        return Observable.create(
                JiraTicketEmitter(
                        jiraClient = jiraClient,
                        jql = jql,
                        searchFields = "summary,project,created,updated,parent,issuetype,status"
                ),
                Emitter.BackpressureMode.BUFFER
        ).flatMap {
            Observable.from(it)
        }.map {
            Ticket.fromRemoteData(
                    code = it.key,
                    description = it.summary,
                    status = it.status.name,
                    parentCode = it?.parent?.key ?: "",
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

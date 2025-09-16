package lt.markmerkk.tickets

import lt.markmerkk.clientextension.JiraClientExt
import lt.markmerkk.JiraUser
import lt.markmerkk.Tags
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.clientextension.toJiraUser
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable
import rx.Single

class JiraTicketSearch {

    fun projectStatuses(
            now: DateTime,
            jiraClient: JiraClientExt
    ): Single<Set<String>> {
        return Observable
                .create(JiraProjectStatusesEmitter(jiraClient), Emitter.BackpressureMode.BUFFER)
                .toSingle()
    }

    fun searchIssues(
        now: DateTime,
        jiraClient: JiraClientExt,
        jql: String
    ): Observable<Ticket> {
        return Observable.create(
                JiraTicketEmitter(
                        jiraClient = jiraClient,
                        jql = jql,
                        searchFields = TICKET_SEARCH_FIELDS.joinToString(separator = ",")
                ),
                Emitter.BackpressureMode.BUFFER
        ).flatMap {
            Observable.from(it)
        }.map {
            val assigneeAsUser: JiraUser = it?.assignee?.toJiraUser() ?: JiraUser.asEmpty()
            val reporterAsUser: JiraUser = it?.reporter?.toJiraUser() ?: JiraUser.asEmpty()
            Ticket.fromRemoteData(
                    code = it.key,
                    description = it.summary,
                    status = it?.status?.name ?: "",
                    assigneeName = assigneeAsUser.identifierAsString(),
                    reporterName = reporterAsUser.identifierAsString(),
                    isWatching = it?.watches?.isWatching ?: false,
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
        val TICKET_SEARCH_FIELDS = listOf(
                "summary",
                "project",
                "created",
                "updated",
                "parent",
                "issuetype",
                "status",
                "assignee",
                "watches",
                "reporter"
        )
    }

}

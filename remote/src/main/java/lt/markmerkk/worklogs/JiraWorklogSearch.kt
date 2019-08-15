package lt.markmerkk.worklogs

import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import net.rcarz.jiraclient.JiraClient
import net.rcarz.jiraclient.WorkLog
import net.rcarz.jiraclient.agile.Worklog
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable

class JiraWorklogSearch(
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings
) {

    fun searchWorlogs(
            now: DateTime,
            jiraClient: JiraClient,
            jql: String,
            startDate: LocalDate,
            endDate: LocalDate
    ): Observable<Pair<Ticket, List<Log>>> {
        return Observable.create(
                JiraWorklogEmitter(
                        jiraClient = jiraClient,
                        jql = jql,
                        searchFields = "summary,project,created,updated,parent,issuetype",
                        start = startDate,
                        end = endDate
                ),
                Emitter.BackpressureMode.BUFFER
        ).map { issueWorklogPair ->
            val issue = issueWorklogPair.issue
            val ticket = Ticket.fromRemoteData(
                    code = issue.key,
                    description = issue.summary,
                    remoteData = RemoteData.fromRemote(
                            fetchTime = now.millis,
                            url = issue.url
                    )
            )
            val worklogs = issueWorklogPair.worklogs
                    .filter {
                        isCurrentUserLog(
                                activeUsername = userSettings.username,
                                worklog = it
                        )
                    }
                    .map {
                        Log.fromRemoteData(
                                timeProvider = timeProvider,
                                code = ticket.code.code,
                                comment = it.comment,
                                started = it.started,
                                timeSpentSeconds = it.timeSpentSeconds,
                                fetchTime = now,
                                url = it.url
                        )
                    }
            logger.debug("Remapping ${ticket.code.code} JIRA worklogs to Log (${worklogs.size} / ${issueWorklogPair.worklogs.size})")
            ticket to worklogs
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.JIRA)
        fun isCurrentUserLog(
                activeUsername: String,
                worklog: WorkLog
        ): Boolean {
            return activeUsername.equals(worklog.author.displayName, ignoreCase = true)
                    || activeUsername.equals(worklog.author.email, ignoreCase = true)
        }
    }

}
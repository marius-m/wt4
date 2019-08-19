package lt.markmerkk.worklogs

import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Tags
import lt.markmerkk.TimeProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import rx.Emitter
import rx.Observable
import rx.Single

class JiraWorklogInteractor(
        private val jiraClientProvider: JiraClientProvider,
        private val timeProvider: TimeProvider,
        private val userSettings: UserSettings
) {

    fun searchWorlogs(
            fetchTime: DateTime,
            jql: String,
            startDate: LocalDate,
            endDate: LocalDate
    ): Observable<Pair<Ticket, List<Log>>> {
        return Observable.create(
                JiraWorklogEmitter(
                        jiraClientProvider = jiraClientProvider,
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
                            fetchTime = fetchTime.millis,
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
                                fetchTime = fetchTime,
                                url = it.url
                        )
                    }
            logger.debug("Remapping ${ticket.code.code} JIRA worklogs to Log (${worklogs.size} / ${issueWorklogPair.worklogs.size})")
            ticket to worklogs
        }
    }

    /**
     * Uploads a worklog.
     * Will throw an exception in the stream if worklog not eligable for upload
     * @throws JiraException whenever upload fails
     * @throws IllegalArgumentException whenever worklog is not valid
     */
    // todo incomplete behaviour, test out before using
    fun uploadWorklog(
            fetchTime: DateTime,
            log: Log
    ): Single<Log> {
        return Single.defer {
            val jiraClient = jiraClientProvider.clientFromCache()
            val issue = jiraClient.getIssue(log.code.code)
            val remoteWorklog = issue.addWorkLog(
                    log.comment,
                    log.time.start,
                    log.time.duration.standardSeconds
            )
            val logAsRemote = Log.fromRemoteData(
                    timeProvider,
                    code = issue.key,
                    started = remoteWorklog.started,
                    comment = remoteWorklog.comment,
                    timeSpentSeconds = remoteWorklog.timeSpentSeconds,
                    fetchTime = fetchTime,
                    url = remoteWorklog.url
            )
            Single.just(logAsRemote)
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
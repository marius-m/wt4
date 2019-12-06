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

    /**
     * Fetches worklogs for time gap. The worklogs returned will exceed [startDate] and [endDate]
     * as it'll bind 1:1 worklogs with their related issues
     */
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
                        searchFields = "summary,project,created,updated,parent,issuetype,status",
                        start = startDate,
                        end = endDate
                ),
                Emitter.BackpressureMode.BUFFER
        ).map { issueWorklogPair ->
            val issue = issueWorklogPair.issue
            val ticket = Ticket.fromRemoteData(
                    code = issue.key,
                    description = issue.summary,
                    status = issue?.status?.name ?: "",
                    parentCode = issue.parent?.key ?: "",
                    remoteData = RemoteData.fromRemote(
                            fetchTime = fetchTime.millis,
                            url = issue.url
                    )
            )
            val worklogs = issueWorklogPair.worklogs
                    .filter {
                        isCurrentUserLog(
                                activeUsername = userSettings.jiraUser().name,
                                worklog = it
                        )
                    }
                    .map {
                        Log.createFromRemoteData(
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

    fun searchWorklogsAsList(
            fetchTime: DateTime,
            jql: String,
            startDate: LocalDate,
            endDate: LocalDate
    ): Single<List<Log>> {
        return searchWorlogs(fetchTime, jql, startDate, endDate)
                .flatMap { Observable.from(it.second) }
                .toList()
                .take(1)
                .toSingle()
    }

    /**
     * Uploads a worklog. Returns [Log] with uploaded remote data
     * Will throw an exception in the stream if worklog not eligible for upload
     * @throws JiraException whenever upload fails
     * @throws IllegalArgumentException whenever worklog is not valid
     */
    // todo: Each worklog upload will create do multiple requests to jira. This can be optimized
    fun uploadWorklog(
            fetchTime: DateTime,
            log: Log
    ): Single<Log> {
        return Single.defer {
            val jiraClient = jiraClientProvider.client()
            val issue = jiraClient.getIssue(log.code.code)
            val remoteWorklog = issue.addWorkLog(
                    log.comment,
                    log.time.start,
                    log.time.duration.standardSeconds
            )
            val logAsRemote = log.appendRemoteData(
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

    /**
     * Deletes worklog
     * Will throw an exception in the stream if worklog not eligable for upload
     * @throws JiraException whenever worklog deletion fails
     * @throws IllegalArgumentException whenever worklog is not valid
     */
    fun delete(
            log: Log
    ): Single<Long> {
        return Single.defer {
            val remoteId = log.remoteData?.remoteId ?: throw IllegalArgumentException("Cannot find worklog id")
            val jiraClient = jiraClientProvider.client()
            val issue = jiraClient.getIssue(log.code.code)
            issue.removeWorklog(remoteId.toString())
            Single.just(remoteId)
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
                    || activeUsername.equals(worklog.author.name, ignoreCase = true)
        }
    }

}
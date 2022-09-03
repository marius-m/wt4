package lt.markmerkk.worklogs

import lt.markmerkk.*
import lt.markmerkk.entities.Log
import lt.markmerkk.entities.RemoteData
import lt.markmerkk.entities.Ticket
import lt.markmerkk.tickets.JiraTicketSearch
import lt.markmerkk.utils.TimedCommentStamper
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
                        searchFields = JiraTicketSearch.TICKET_SEARCH_FIELDS
                                .joinToString(separator = ","),
                        start = startDate,
                        end = endDate
                ),
                Emitter.BackpressureMode.BUFFER
        ).map { issueWorklogPair ->
            val issue = issueWorklogPair.issue
            val assigneeAsUser = issue.assignee?.toJiraUser() ?: JiraUser.asEmpty()
            val reporterAsuser = issue.reporter?.toJiraUser() ?: JiraUser.asEmpty()
            val ticket = Ticket.fromRemoteData(
                    code = issue.key,
                    description = issue.summary,
                    status = issue.status?.name ?: "",
                    assigneeName = assigneeAsUser.identifierAsString(),
                    reporterName = reporterAsuser.identifierAsString(),
                    isWatching = issue.watches?.isWatching ?: false,
            parentCode = issue.parent?.key ?: "",
                    remoteData = RemoteData.fromRemote(
                            fetchTime = fetchTime.millis,
                            url = issue.url
                    )
            )
            val activeUserIdentifier = userSettings.jiraUser().identifierAsString()
            val worklogs = issueWorklogPair.worklogs
                    .filter {
                        isCurrentUserLog(
                                activeIdentifier = activeUserIdentifier,
                                worklog = it
                        )
                    }
                    .map { workLog ->
                        val comment: String = workLog.comment ?: ""
                        val noStampComment = TimedCommentStamper.removeStamp(comment)
                        val jiraUser = workLog.author.toJiraUser()
                        Log.createFromRemoteData(
                                timeProvider = timeProvider,
                                code = ticket.code.code,
                                comment = noStampComment,
                                started = workLog.started,
                                timeSpentSeconds = workLog.timeSpentSeconds,
                                fetchTime = fetchTime,
                                url = workLog.url,
                                author = jiraUser.identifierAsString()
                        )
                    }
            logger.debug("Remapping ${ticket.code.code} JIRA worklogs to Log (${worklogs.size} / ${issueWorklogPair.worklogs.size})")
            ticket to worklogs
        }
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
            val commentWithTimeStamp = TimedCommentStamper
                    .addStamp(log.time.start, log.time.end, log.comment)
            val remoteWorklog = issue.addWorkLog(
                    commentWithTimeStamp,
                    log.time.start,
                    log.time.duration.standardSeconds
            )
            val noStampRemoteComment = TimedCommentStamper
                    .removeStamp(remoteWorklog.comment)
            val logAsRemote = log.appendRemoteData(
                    timeProvider,
                    code = issue.key,
                    started = remoteWorklog.started,
                    comment = noStampRemoteComment,
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
                activeIdentifier: String,
                worklog: WorkLog
        ): Boolean {
            val jiraUser = worklog.author.toJiraUser()
            if (jiraUser.isEmpty() || activeIdentifier.isEmpty()) {
                return false
            }
            return activeIdentifier.equals(jiraUser.name, ignoreCase = true)
                    || activeIdentifier.equals(jiraUser.displayName, ignoreCase = true)
                    || activeIdentifier.equals(jiraUser.email, ignoreCase = true)
                    || activeIdentifier.equals(jiraUser.accountId, ignoreCase = true)
        }
    }

}
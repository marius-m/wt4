package lt.markmerkk.entities

import net.rcarz.jiraclient.Issue
import org.joda.time.DateTime

fun Issue.toTicket(
        now: DateTime
): Ticket {
    return Ticket.fromRemoteData(
            code = this.key,
            description = this.summary,
            remoteData = RemoteData.fromRemote(
                    fetchTime = now.millis,
                    url = this.self
            )
    )
}

fun Ticket.bindIssue(
        now: DateTime,
        issue: Issue
): Ticket {
    return this.bindRemoteData(
            now = now,
            remoteProjectKey = issue.key,
            remoteDescription = issue.summary,
            remoteUri = issue.self,
            remoteIdUrl = issue.id
    )
}

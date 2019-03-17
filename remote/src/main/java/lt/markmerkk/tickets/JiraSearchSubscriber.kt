package lt.markmerkk.tickets

import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import rx.Observable

interface JiraSearchSubscriber {
    fun workedIssuesObservable(start: Long, end: Long): Observable<Issue.SearchResult>
    fun userIssuesObservable(): Observable<Issue.SearchResult>
}
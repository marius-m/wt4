package lt.markmerkk

import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraSearchSubscriber {
    fun workedIssuesObservable(start: Long, end: Long): Observable<Issue.SearchResult>
    fun userIssuesObservable(): Observable<Issue.SearchResult>
}
package lt.markmerkk

import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import org.joda.time.DateTime
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraSearchSubsciber {
    fun searchResultObservable(start: Long, end: Long): Observable<Issue.SearchResult>
}
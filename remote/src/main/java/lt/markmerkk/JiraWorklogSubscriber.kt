package lt.markmerkk

import lt.markmerkk.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraWorklogSubscriber {
    fun worklogResultObservable(searchResult: Issue.SearchResult): Observable<JiraWork>
}
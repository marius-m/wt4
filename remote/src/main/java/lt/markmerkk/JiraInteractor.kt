package lt.markmerkk

import lt.markmerkk.JiraWork
import net.rcarz.jiraclient.Issue
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraInteractor {
    fun jiraWorks(start: Long, end: Long): Observable<List<JiraWork>>
    fun jiraIssues(): Observable<Issue>
}
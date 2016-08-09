package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.Issue
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-07-09
 */
interface JiraInteractor {
    fun jiraRemoteWorks(start: Long, end: Long): Observable<List<JiraWork>>
    fun jiraLocalWorks(): Observable<List<SimpleLog>>
    fun jiraIssues(): Observable<List<Issue>>
}
package lt.markmerkk.interactors

import lt.markmerkk.entities.LocalIssue
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
interface IssueSearchInteractor {
    fun searchIssues(byPhrase: String): Observable<List<LocalIssue>>
    fun allIssues(): Observable<List<LocalIssue>>
}
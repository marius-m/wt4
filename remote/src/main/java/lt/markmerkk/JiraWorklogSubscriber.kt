package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
import rx.Observable

interface JiraWorklogSubscriber {
    fun worklogResultObservable(searchResult: Issue.SearchResult): Observable<JiraWork>
}
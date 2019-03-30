package lt.markmerkk

import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import rx.Observable

interface JiraInteractor {
    fun jiraRemoteWorks(start: Long, end: Long): Observable<List<JiraWork>>
    fun jiraLocalWorks(): Observable<List<SimpleLog>>
}
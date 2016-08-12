package lt.markmerkk.interactors

import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.QueryListJob
import lt.markmerkk.utils.IssueSplit
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
class IssueSearchInteractorImpl(
        private val executor: IExecutor
) : IssueSearchInteractor {

    private val issueSplitter = IssueSplit()

    override fun searchIssues(byPhrase: String): Observable<List<LocalIssue>> {
        val phraseMap = issueSplitter.split(byPhrase)
        val queryListJob = QueryListJob(LocalIssue::class.java
        ) {
            String.format("(%s like '%%%s%%' OR %s like '%%%s%%') ORDER BY %s DESC",
                    LocalIssue.KEY_DESCRIPTION, phraseMap[IssueSplit.DESCRIPTION_KEY],
                    LocalIssue.KEY_KEY, phraseMap[IssueSplit.KEY_KEY],
                    LocalIssue.KEY_CREATE_DATE)
        }
        executor.execute(queryListJob)
        return Observable.just(queryListJob.result())
    }

    override fun allIssues(): Observable<List<LocalIssue>> {
        val queryListJob = QueryListJob(LocalIssue::class.java)
        executor.execute(queryListJob)
        return Observable.just(queryListJob.result())
    }

}
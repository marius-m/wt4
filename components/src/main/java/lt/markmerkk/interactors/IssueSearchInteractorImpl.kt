package lt.markmerkk.interactors

import lt.markmerkk.LikeQueryGenerator
import lt.markmerkk.LikeQueryGeneratorImpl
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.QueryListJob
import lt.markmerkk.entities.jobs.RowCountJob
import lt.markmerkk.utils.IssueSplitImpl
import lt.markmerkk.utils.LogUtils
import org.slf4j.LoggerFactory
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
class IssueSearchInteractorImpl(
        private val executor: IExecutor
) : IssueSearchInteractor {

    private val issueSplitter = IssueSplitImpl()
    private val descriptionQueryGenerator: LikeQueryGenerator = LikeQueryGeneratorImpl(LocalIssue.KEY_DESCRIPTION)
    private val keyQueryGenerator: LikeQueryGenerator = LikeQueryGeneratorImpl(LocalIssue.KEY_KEY)

    override fun searchIssues(byPhrase: String): Observable<List<LocalIssue>> {
        val queryListJob = QueryListJob(LocalIssue::class.java) {
            val query = String.format(
                    "ORDER BY %s DESC",
                    LocalIssue.KEY_CREATE_DATE
            )
            logger.debug("Running query $query")
            query
        }
        executor.execute(queryListJob)
        return Observable.just(queryListJob.result())
    }

    override fun allIssues(): Observable<List<LocalIssue>> {
        val queryListJob = QueryListJob(LocalIssue::class.java)
        executor.execute(queryListJob)
        return Observable.just(queryListJob.result())
    }

    override fun issueCount(): Observable<Int> {
        val rowJobCount = RowCountJob(LocalIssue::class.java)
        executor.execute(rowJobCount)
        return Observable.just(rowJobCount.result())
    }

    companion object {
        val logger = LoggerFactory.getLogger(IssueSearchInteractorImpl::class.java)!!
    }

}
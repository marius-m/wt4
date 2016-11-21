package lt.markmerkk.interactors

import lt.markmerkk.LikeQueryGenerator
import lt.markmerkk.LikeQueryGeneratorImpl
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.QueryListJob
import lt.markmerkk.entities.jobs.RowCountJob
import lt.markmerkk.utils.IssueSplit
import org.slf4j.LoggerFactory
import rx.Observable

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
class IssueSearchInteractorImpl(
        private val executor: IExecutor
) : IssueSearchInteractor {

    private val issueSplitter = IssueSplit()
    private val descriptionQueryGenerator: LikeQueryGenerator = LikeQueryGeneratorImpl(LocalIssue.KEY_DESCRIPTION)

    override fun searchIssues(byPhrase: String): Observable<List<LocalIssue>> {
        val phraseMap = issueSplitter.split(byPhrase)
        val queryListJob = QueryListJob(LocalIssue::class.java) {
            val descriptionClauses = descriptionQueryGenerator.genClauses(phraseMap[IssueSplit.DESCRIPTION_KEY]!!)
            val descriptionQuery = descriptionQueryGenerator.genQuery(descriptionClauses)
            val query = String.format(
                    "$descriptionQuery ORDER BY %s DESC",
                    LocalIssue.KEY_CREATE_DATE
            )
            logger.debug("Running query $query")
            query
//            String.format("(%s like '%%%s%%' OR %s like '%%%s%%') ORDER BY %s DESC",
//                    LocalIssue.KEY_DESCRIPTION, phraseMap[IssueSplit.DESCRIPTION_KEY],
//                    LocalIssue.KEY_KEY, phraseMap[IssueSplit.KEY_KEY],
//                    LocalIssue.KEY_CREATE_DATE)
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
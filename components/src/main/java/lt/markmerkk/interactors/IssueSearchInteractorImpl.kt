package lt.markmerkk.interactors

import lt.markmerkk.LikeQueryGenerator
import lt.markmerkk.LikeQueryGeneratorImpl
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.jobs.QueryListJob
import lt.markmerkk.entities.jobs.RowCountJob
import lt.markmerkk.utils.IssueSplit
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

    private val issueSplitter = IssueSplit()
    private val descriptionQueryGenerator: LikeQueryGenerator = LikeQueryGeneratorImpl(LocalIssue.KEY_DESCRIPTION)
    private val keyQueryGenerator: LikeQueryGenerator = LikeQueryGeneratorImpl(LocalIssue.KEY_KEY)

    override fun searchIssues(byPhrase: String): Observable<List<LocalIssue>> {
        val phraseMap = issueSplitter.split(byPhrase)
        val queryListJob = QueryListJob(LocalIssue::class.java) {
            // todo move this in testable env
            val descriptionClauses = descriptionQueryGenerator.genClauses(phraseMap[IssueSplit.DESCRIPTION_KEY]!!)
            val descriptionQuery = descriptionQueryGenerator.genQuery(descriptionClauses)
            val possibleIssueTitle = LogUtils.validateTaskTitle(phraseMap[IssueSplit.DESCRIPTION_KEY] ?: "") ?: ""
            val keyClauses = keyQueryGenerator.genClauses(possibleIssueTitle)
            val keyQuery = keyQueryGenerator.genQuery(keyClauses)
            val fullQuery = descriptionQueryGenerator.genQuery(listOf(descriptionQuery, keyQuery))
            val query = String.format(
                    "$fullQuery ORDER BY %s DESC",
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
package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.LocalIssueBuilder
import net.rcarz.jiraclient.Issue
import org.slf4j.LoggerFactory

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
class RemoteIssuePullImpl(
        private val remoteMergeExecutor: RemoteMergeExecutor<LocalIssue, Issue>,
        private val filter: JiraFilter<Issue>,
        private val downloadMillis: Long,
        private val issue: Issue
) : RemoteIssuePull {

    override fun call(): Issue {
        try {
            if (filter.valid(issue)) {
                val oldIssue = remoteMergeExecutor.localEntityFromRemote(issue)
                if (oldIssue == null) {
                    remoteMergeExecutor.create(
                            LocalIssueBuilder(issue)
                                    .setDownloadMillis(downloadMillis)
                                    .build()
                    )
                } else {
                    remoteMergeExecutor.update(
                            LocalIssueBuilder(oldIssue)
                                    .setDownloadMillis(downloadMillis)
                                    .build()
                    )
                }
            }
        } catch (e: JiraFilter.FilterErrorException) {
            logger.debug("Skip issue due to ${e.message}")
        }
        return issue
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RemoteIssuePullImpl::class.java)!!
    }

}
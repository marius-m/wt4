package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.LocalIssueBuilder
import net.rcarz.jiraclient.Issue

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
class RemoteIssuePullImpl(
        private val remoteMergeExecutor: RemoteMergeExecutor<LocalIssue, Issue>,
        private val filter: JiraFilter<Issue>,
        private val issue: Issue
) : RemoteIssuePull {

    override fun call(): Issue {
        remoteMergeExecutor.create(
                LocalIssueBuilder(issue).build()
        )
        return issue
    }
}
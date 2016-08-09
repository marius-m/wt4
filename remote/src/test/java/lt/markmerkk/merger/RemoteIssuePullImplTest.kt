package lt.markmerkk.merger

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.LocalIssue
import net.rcarz.jiraclient.Issue
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class RemoteIssuePullImplTest {

    val executor: RemoteMergeExecutor<LocalIssue, Issue> = mock()
    val filter: JiraFilter<Issue> = mock()

    @Test
    fun validNewIssue_triggerInsertNew() {
        // Arrange
        val validIssue: Issue = mock()
        val pull = RemoteIssuePullImpl(executor, filter, validIssue)

        // Act
        pull.call()

        // Assert
        verify(executor).create(any())
        verify(executor, never()).update(any())
    }
}
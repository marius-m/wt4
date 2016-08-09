package lt.markmerkk.merger

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.LocalIssue
import lt.markmerkk.entities.LocalIssueBuilder
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.Project
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class RemoteIssuePullImplTest {

    val executor: RemoteMergeExecutor<LocalIssue, Issue> = mock()
    val filter: JiraFilter<Issue> = mock()
    val validIssue: Issue = mock()
    lateinit var validOldIssue: LocalIssue

    @Before
    fun setUp() {
        doReturn(true).whenever(filter).valid(any())
        val validProject: Project = mock()
        whenever(validIssue.project).thenReturn(validProject)
        whenever(validIssue.createdDate).thenReturn(Date(1000))
        whenever(validIssue.updatedDate).thenReturn(Date(2000))
        whenever(validIssue.key).thenReturn("valid_key")
        whenever(validIssue.summary).thenReturn("valid_summary")
        whenever(validIssue.self).thenReturn("valid_self")
        whenever(validProject.key).thenReturn("valid_key")

        validOldIssue = LocalIssueBuilder(validIssue).build()
    }

    @Test
    fun validNewIssue_triggerInsertNew() {
        // Arrange
        val pull = RemoteIssuePullImpl(executor, filter, validIssue)

        // Act
        pull.call()

        // Assert
        verify(executor).create(any())
        verify(executor, never()).update(any())
    }

    @Test
    fun validOldIssue_triggerUpdate() {
        // Arrange
        whenever(executor.localEntityFromRemote(any()))
                .thenReturn(validOldIssue)
        val pull = RemoteIssuePullImpl(executor, filter, validIssue)

        // Act
        pull.call()

        // Assert
        verify(executor, never()).create(any())
        verify(executor).update(any())
    }

    @Test
    fun validateError_doNotTrigger() {
        // Arrange
        reset(filter)
        whenever(filter.valid(any()))
                .thenThrow(JiraFilter.FilterErrorException("validate error"))
        whenever(executor.localEntityFromRemote(any()))
                .thenReturn(validOldIssue)
        val pull = RemoteIssuePullImpl(executor, filter, validIssue)

        // Act
        pull.call()

        // Assert
        verify(executor, never()).create(any())
        verify(executor, never()).update(any())
    }

    @Test
    fun validateFalse_doNotTrigger() {
        // Arrange
        reset(filter)
        whenever(filter.valid(any())).thenReturn(false)
        whenever(executor.localEntityFromRemote(any()))
                .thenReturn(validOldIssue)
        val pull = RemoteIssuePullImpl(executor, filter, validIssue)

        // Act
        pull.call()

        // Assert
        verify(executor, never()).create(any())
        verify(executor, never()).update(any())
    }
}
package lt.markmerkk.entities

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.entities.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-09
 */
class JiraWorkValidTest {
    @Test
    fun valid_returnTrue() {
        // Arrange
        val fakeIssue: Issue = mock()
        doReturn("valid_key").whenever(fakeIssue).key
        val fakeWorklogs: List<WorkLog> = mock()

        // Act
        val result = JiraWork(
                issue = fakeIssue,
                worklogs = fakeWorklogs
        )

        // Assert
        assertTrue(result.valid())
    }

    @Test
    fun brokenIssueNoKey_returnFalse() {
        // Arrange
        val fakeIssue: Issue = mock()
        val fakeWorklogs: List<WorkLog> = mock()

        // Act
        val result = JiraWork(
                issue = fakeIssue,
                worklogs = fakeWorklogs
        )

        // Assert
        assertFalse(result.valid())
    }

    @Test
    fun nullIssue_returnFalse() {
        // Arrange
        val fakeWorklogs: List<WorkLog> = mock()

        // Act
        val result = JiraWork(
                issue = null,
                worklogs = fakeWorklogs
        )

        // Assert
        assertFalse(result.valid())
    }

    @Test
    fun nullWorklogs_returnFalse() {
        // Arrange
        val fakeIssue: Issue = mock()

        // Act
        val result = JiraWork(
                issue = fakeIssue,
                worklogs = null
        )

        // Assert
        assertFalse(result.valid())
    }
}
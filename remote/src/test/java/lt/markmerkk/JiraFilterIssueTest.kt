package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.Project
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class JiraFilterIssueTest {

    val filter = JiraFilterIssue()

    @Test
    fun valid_returnTrue() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_key").whenever(validProject).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn(Date(1000)).whenever(validIssue).updatedDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertTrue(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noUpdateDate_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_key").whenever(validProject).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noSelf_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_key").whenever(validProject).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn(Date(1000)).whenever(validIssue).updatedDate

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noCreateDate_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_key").whenever(validProject).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).updatedDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noSummary_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_key").whenever(validProject).key
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn(Date(1000)).whenever(validIssue).updatedDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noKey_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validProject).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn(Date(1000)).whenever(validIssue).updatedDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noProjectKey_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn(validProject).whenever(validIssue).project
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn(Date(1000)).whenever(validIssue).updatedDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noProject_throwError() {
        // Arrange
        val validIssue: Issue = mock()
        val validProject: Project = mock()
        doReturn("valid_key").whenever(validIssue).key
        doReturn("valid_key").whenever(validProject).key
        doReturn("valid_summary").whenever(validIssue).summary
        doReturn(Date(1000)).whenever(validIssue).createdDate
        doReturn(Date(1000)).whenever(validIssue).updatedDate
        doReturn("valid_self").whenever(validIssue).self

        // Act
        val result = filter.valid(validIssue)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun null_throwError() {
        // Arrange
        // Act
        val result = filter.valid(null)

        // Assert
        assertFalse(result)
    }
}
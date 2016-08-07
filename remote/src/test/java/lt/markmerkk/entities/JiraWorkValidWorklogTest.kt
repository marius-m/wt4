package lt.markmerkk.entities

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.JiraWork
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.WorkLog
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-07-10
 */
class JiraWorkValidWorklogTest {
    @Test
    fun valid_returnTrue() {
        // Arrange
        val fakeWorklog: WorkLog = mock()
        doReturn(Date(1000)).whenever(fakeWorklog).started
        val work = JiraWork()

        // Act
        val result = work.validWorklog(fakeWorklog)

        // Assert
        assertTrue(result)
    }

    @Test
    fun invalidNoStart_returnFalse() {
        // Arrange
        val fakeWorklog: WorkLog = mock()
        val work = JiraWork()

        // Act
        val result = work.validWorklog(fakeWorklog)

        // Assert
        assertFalse(result)
    }
}
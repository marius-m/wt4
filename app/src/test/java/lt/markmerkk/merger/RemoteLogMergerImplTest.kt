package lt.markmerkk.merger

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraFilter
import lt.markmerkk.JiraWork
import lt.markmerkk.storage2.SimpleLog
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
class RemoteLogMergerImplTest {

    val remoteFilter: JiraFilter<WorkLog> = mock()
    val mergeExecutor: RemoteMergeExecutor = mock()

    @Test
    fun filterInvalidWorklog_noChange() {
        // Arrange
        val fakeIssue: Issue = mock()
        doReturn("valid_key").whenever(fakeIssue).key
        val fakeWorkLog: WorkLog = mock()
        doReturn(Date(1000)).whenever(fakeWorkLog).started
        doReturn(false).whenever(remoteFilter).valid(any())
        val puller = RemoteLogMergerImpl(
                mergeExecutor,
                remoteFilter,
                JiraWork(fakeIssue, listOf(fakeWorkLog))
        )

        // Act
        puller.call()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }

    @Test
    fun validNewEntry_triggerNew() {
        // Arrange
        val fakeIssue: Issue = mock()
        doReturn("valid_key").whenever(fakeIssue).key
        val fakeWorkLog: WorkLog = mock()
        doReturn(Date(1000)).whenever(fakeWorkLog).started
        doReturn(true).whenever(remoteFilter).valid(any())
        val puller = RemoteLogMergerImpl(
                mergeExecutor,
                remoteFilter,
                JiraWork(fakeIssue, listOf(fakeWorkLog))
        )

        // Act
        puller.call()

        // Assert
        verify(mergeExecutor).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }

    @Test
    fun validOldEntry_triggerUpdate() {
        // Arrange
        val fakeIssue: Issue = mock()
        doReturn("valid_key").whenever(fakeIssue).key
        val fakeWorkLog: WorkLog = mock()
        doReturn("valid_self").whenever(fakeWorkLog).self
        doReturn(Date(1000)).whenever(fakeWorkLog).started
        val oldLog: SimpleLog = mock()
        doReturn(oldLog).whenever(mergeExecutor).localEntityFromRemote(any())
        doReturn(true).whenever(remoteFilter).valid(any())
        val puller = RemoteLogMergerImpl(
                mergeExecutor,
                remoteFilter,
                JiraWork(fakeIssue, listOf(fakeWorkLog))
        )

        // Act
        puller.call()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor).updateLog(any())
    }

    @Test
    fun invalidEntryIssueBrokenNoKey_noTrigger() {
        // Arrange
        val fakeIssue: Issue = mock()
        val fakeWorkLog: WorkLog = mock()
        doReturn(Date(1000)).whenever(fakeWorkLog).started
        doReturn(true).whenever(remoteFilter).valid(any())
        val puller = RemoteLogMergerImpl(
                mergeExecutor,
                remoteFilter,
                JiraWork(fakeIssue, listOf(
                        fakeWorkLog
                )) // invalid entry
        )

        // Act
        puller.call()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }

    @Test
    fun invalidEntryIssueNull_noTrigger() {
        // Arrange
        val puller = RemoteLogMergerImpl(
                mergeExecutor,
                remoteFilter,
                JiraWork(null, emptyList()) // invalid entry
        )

        // Act
        puller.call()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }

    @Test
    fun invalidEntryNullWorklogs_noTrigger() {
        // Arrange
        val fakeIssue: Issue = mock()
        val puller = RemoteLogMergerImpl(
                mergeExecutor,
                remoteFilter,
                JiraWork(fakeIssue, null) // invalid entry
        )

        // Act
        puller.call()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }
}
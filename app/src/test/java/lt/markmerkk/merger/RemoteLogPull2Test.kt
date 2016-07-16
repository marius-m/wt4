package lt.markmerkk.merger

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.entities.JiraWork
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
class RemoteLogPull2Test {
    @Test
    fun validNewEntry_triggerNew() {
        // Arrange
        val fakeIssue: Issue = mock()
        doReturn("valid_key").whenever(fakeIssue).key
        val fakeWorkLog: WorkLog = mock()
        doReturn(Date(1000)).whenever(fakeWorkLog).started
        val mergeExecutor: RemoteMergeExecutor = mock()
        val puller = RemoteLogPull2(
                mergeExecutor,
                JiraWork(fakeIssue, listOf(
                        fakeWorkLog
                ))
        )

        // Act
        puller.run()

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
        val mergeExecutor: RemoteMergeExecutor = mock()
        val oldLog: SimpleLog = mock()
        doReturn(oldLog).whenever(mergeExecutor).localEntityFromRemote(any())
        val puller = RemoteLogPull2(
                mergeExecutor,
                JiraWork(fakeIssue, listOf(
                        fakeWorkLog
                ))
        )

        // Act
        puller.run()

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
        val mergeExecutor: RemoteMergeExecutor = mock()
        val puller = RemoteLogPull2(
                mergeExecutor,
                JiraWork(fakeIssue, listOf(
                        fakeWorkLog
                )) // invalid entry
        )

        // Act
        puller.run()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }

    @Test
    fun invalidEntryIssueNull_noTrigger() {
        // Arrange
        val mergeExecutor: RemoteMergeExecutor = mock()
        val puller = RemoteLogPull2(
                mergeExecutor,
                JiraWork(null, emptyList()) // invalid entry
        )

        // Act
        puller.run()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }

    @Test
    fun invalidEntryNullWorklogs_noTrigger() {
        // Arrange
        val fakeIssue: Issue = mock()
        val mergeExecutor: RemoteMergeExecutor = mock()
        val puller = RemoteLogPull2(
                mergeExecutor,
                JiraWork(fakeIssue, null) // invalid entry
        )

        // Act
        puller.run()

        // Assert
        verify(mergeExecutor, never()).createLog(any())
        verify(mergeExecutor, never()).updateLog(any())
    }
}
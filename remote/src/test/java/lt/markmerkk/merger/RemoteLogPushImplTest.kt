package lt.markmerkk.merger

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.JiraException
import net.rcarz.jiraclient.WorkLog
import org.junit.Ignore
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class RemoteLogPushImplTest {

    val executor: RemoteMergeExecutor<SimpleLog, WorkLog> = mock()
    val client: RemoteMergeClient = mock()
    val uploadValidator: JiraFilter<SimpleLog> = mock()

    @Test
    fun valid_triggerUpload() {
        // Arrange
        val validOutWorklog: WorkLog = mock()
        doReturn(validOutWorklog).whenever(client).uploadLog(any())
        doReturn(true).whenever(uploadValidator).valid(any())
        val validLog = SimpleLog()
        val push = RemoteLogPushImpl(
                remoteMergeClient = client,
                remoteMergeExecutor = executor,
                uploadValidator = uploadValidator,
                localLog = validLog
        )

        // Act
        push.call()

        // Assert
        verify(client).uploadLog(eq(validLog))
        verify(executor).recreate(eq(validLog), any())
    }

    @Test
    fun invalidLog_triggerError() {
        // Arrange
        val invalidLog = SimpleLog()
        doThrow(JiraFilter.FilterErrorException("invalid_log")).whenever(uploadValidator).valid(any())
        val push = RemoteLogPushImpl(
                remoteMergeClient = client,
                remoteMergeExecutor = executor,
                uploadValidator = uploadValidator,
                localLog = invalidLog
        )

        // Act
        push.call()

        // Assert
        verify(client, never()).uploadLog(any())
        verify(executor).markAsError(eq(invalidLog), any())
    }

    @Test
    fun errorUploading_triggerError() {
        // Arrange
        val validLog = SimpleLog()
        doReturn(true).whenever(uploadValidator).valid(any())
        doThrow(JiraException("error_uploading")).whenever(client).uploadLog(any())
        val push = RemoteLogPushImpl(
                remoteMergeClient = client,
                remoteMergeExecutor = executor,
                uploadValidator = uploadValidator,
                localLog = validLog
        )

        // Act
        push.call()

        // Assert
        verify(client).uploadLog(any())
        verify(executor).markAsError(eq(validLog), any())
        verify(executor, never()).recreate(any(), any())
    }

    @Test
    fun validationFail_notAnError_doNotUpload() {
        // Arrange
        val validLog = SimpleLog()
        doReturn(false).whenever(uploadValidator).valid(any())
        val push = RemoteLogPushImpl(
                remoteMergeClient = client,
                remoteMergeExecutor = executor,
                uploadValidator = uploadValidator,
                localLog = validLog
        )

        // Act
        push.call()

        // Assert
        verify(client, never()).uploadLog(any())
        verify(executor, never()).markAsError(eq(validLog), any())
        verify(executor, never()).recreate(any(), any())
    }
}
package lt.markmerkk.merger

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.WorkLog
import org.junit.Ignore
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class RemoteLogPushImplTest {

    val executor: RemoteMergeExecutor = mock()
    val client: RemoteMergeClient = mock()
    val uploadValidator: JiraFilter<SimpleLog> = mock()

    @Test
    @Ignore // todo : Incomplete test
    fun valid_triggerUpload() {
        // Arrange
        val validOutWorklog: WorkLog = mock()
        doReturn(validOutWorklog).whenever(client).uploadLog(any())
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
        verify(executor).recreateLog(eq(validLog), any())
    }

    @Test
    @Ignore // todo : Incomplete test
    fun noIssueKey_triggerError() {
        // Arrange
        val noIssueLog = SimpleLog()
        val push = RemoteLogPushImpl(
                remoteMergeClient = client,
                remoteMergeExecutor = executor,
                uploadValidator = uploadValidator,
                localLog = noIssueLog
        )

        // Act
        push.call()

        // Assert
        verify(client, never()).uploadLog(any())
        verify(executor).markAsError(eq(noIssueLog), any())
    }
}
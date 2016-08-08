package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.entities.SimpleLogBuilder
import net.rcarz.jiraclient.WorkLog
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-08
 */
class JiraUploadValidatorTest {

    val validator = JiraUploadValidator()

    @Test
    fun valid_returnTrue() {
        // Arrange
        val validSimpleLog = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setComment("valid_comment")
                .setTask("TT-12")
                .build()

        // Act
        val result = validator.valid(validSimpleLog)

        // Assert
        assertTrue(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun nullInput_returnFalse() {
        // Arrange
        // Act
        val result = validator.valid(null)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noIssueKey_returnFalse() {
        // Arrange
        val noIssueLog = SimpleLogBuilder()
                .setComment("valid_comment")
                .setStart(1000)
                .setEnd(2000)
                .build()

        // Act
        val result = validator.valid(noIssueLog)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun noComment_returnFalse() {
        // Arrange
        val noCommentLog = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("TT-12")
                .build()

        // Act
        val result = validator.valid(noCommentLog)

        // Assert
        assertFalse(result)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun alreadyHasError_returnFalse() {
        // Arrange
        val logWithError = SimpleLogBuilder()
                .setStart(1000)
                .setEnd(2000)
                .setTask("TT-12")
                .setComment("valid_comment")
                .buildWithError("some_error")

        // Act
        val result = validator.valid(logWithError)

        // Assert
        assertFalse(result)
    }

    @Test
    fun alreadyInSync_returnFalse() {
        // Arrange
        val validRemoteLog: WorkLog = mock()
        doReturn(Date(1000)).whenever(validRemoteLog).started
        doReturn(60).whenever(validRemoteLog).timeSpentSeconds
        doReturn("valid_comment").whenever(validRemoteLog).comment
        val fromRemoteLog = SimpleLogBuilder("TT-12", validRemoteLog)
                .build()

        // Act
        val result = validator.valid(fromRemoteLog)

        // Assert
        assertFalse(result)
    }
}
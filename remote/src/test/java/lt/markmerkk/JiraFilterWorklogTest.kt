package lt.markmerkk

import com.nhaarman.mockito_kotlin.whenever
import net.rcarz.jiraclient.User
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by mariusmerkevicius on 1/30/16.
 */
class JiraFilterWorklogTest {
    lateinit var user: User
    lateinit var validWorklog: WorkLog
    val worklogTime: Long = 1000

    @Before
    fun setUp() {
        user = mock(User::class.java)
        validWorklog = mock(WorkLog::class.java)
        doReturn(Date(DateTime(worklogTime).millis)).whenever(validWorklog).started
        doReturn(user).whenever(validWorklog).author
        doReturn("valid_username").whenever(user).email
    }

    @Test
    fun valid_returnTrue() {
        // Arrange
        val filterer = JiraFilterWorklog("valid_username", 500L, 2000L)

        // Act
        val out = filterer.valid(validWorklog)

        // Assert
        assertTrue(out)
    }

    @Test
    fun endEqualCreate_returnTrue() {
        // Arrange
        val filterer = JiraFilterWorklog("valid_username", 500L, 1000L)

        // Act
        val out = filterer.valid(validWorklog)

        // Assert
        assertTrue(out)
    }

    @Test
    fun startEqualCreate_returnTrue() {
        // Arrange
        val filterer = JiraFilterWorklog("valid_username", 1000L, 2000L)

        // Act
        val out = filterer.valid(validWorklog)

        // Assert
        assertTrue(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun endBeforeCreate_returnFalse() {
        // Arrange
        val filterer = JiraFilterWorklog("valid_username", 500L, 900L)

        // Act
        val out = filterer.valid(validWorklog)

        // Assert
        assertFalse(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun startAfterCreate_returnFalse() {
        // Arrange
        val filterer = JiraFilterWorklog("valid_username", 1200L, 2000L)

        // Act
        val out = filterer.valid(validWorklog)

        // Assert
        assertFalse(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun invalidUser_returnFalse() {
        // Arrange
        val filterer = JiraFilterWorklog("invalid_username", 500L, 2000L)

        // Act
        val out = filterer.valid(validWorklog)

        // Assert
        assertFalse(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun nullWorkLog_returnFalse() {
        // Arrange
        val filterer = JiraFilterWorklog("valid_username", 500L, 2000L)

        // Act
        val out = filterer.valid(null)

        // Assert
        assertFalse(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun invalidWorkLogStartedNull_returnFalse() {
        // Arrange
        val invalidWorklog = mock(WorkLog::class.java)
        doReturn(null).whenever(invalidWorklog).started
        doReturn(user).whenever(invalidWorklog).author
        val filterer = JiraFilterWorklog("valid_username", 500L, 2000L)

        // Act
        val out = filterer.valid(invalidWorklog)

        // Assert
        assertFalse(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun invalidWorkLogAuthorNull_returnFalse() {
        // Arrange
        val invalidWorklog = mock(WorkLog::class.java)
        doReturn(Date(DateTime(worklogTime).millis)).whenever(invalidWorklog).started
        doReturn(null).whenever(invalidWorklog).author
        val filterer = JiraFilterWorklog("valid_username", 500L, 2000L)

        // Act
        val out = filterer.valid(invalidWorklog)

        // Assert
        assertFalse(out)
    }

    @Test(expected = JiraFilter.FilterErrorException::class)
    fun invalidWorkLogAuthorNameNull_returnFalse() {
        // Arrange
        val invalidWorklog = mock(WorkLog::class.java)
        doReturn(Date(DateTime(worklogTime).millis)).whenever(invalidWorklog).started
        doReturn(user).whenever(invalidWorklog).author
        doReturn(null).whenever(user).email
        val filterer = JiraFilterWorklog("valid_username", 500L, 2000L)

        // Act
        val out = filterer.valid(invalidWorklog)

        // Assert
        assertFalse(out)
    }
}
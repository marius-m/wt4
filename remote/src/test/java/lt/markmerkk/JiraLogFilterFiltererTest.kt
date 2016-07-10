package lt.markmerkk

import java.util.Date
import net.rcarz.jiraclient.User
import net.rcarz.jiraclient.WorkLog
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Created by mariusmerkevicius on 1/30/16.
 */
class JiraLogFilterFiltererTest {
    private var user: User? = null
    private var worklog: WorkLog? = null

    @Before
    fun setUp() {
        user = mock(User::class.java)
        worklog = mock(WorkLog::class.java)
        val worklogTime = DateTime(1000)
        doReturn(Date(worklogTime.millis)).`when`<WorkLog>(worklog).started
        doReturn(user).`when`<WorkLog>(worklog).author
        doReturn("asdf").`when`<User>(user).name
    }

    @Test
    fun filterLog_valid_shouldReturnWorklog() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(500), DateTime(2000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNotNull(out)
    }

    @Test
    fun filterLog_endEqualCreate_shouldReturnWorklog() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(500), DateTime(1000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNotNull(out)
    }

    @Test
    fun filterLog_startEqualCreate_shouldReturnWorklog() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(1000), DateTime(2000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNotNull(out)
    }

    @Test
    fun filterLog_endBeforeCreate_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(500), DateTime(900))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNull(out)
    }

    @Test
    fun filterLog_startAfterCreate_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(1200), DateTime(2000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNull(out)
    }

    @Test
    fun filterLog_invalidUser_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer("null", DateTime(500), DateTime(2000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNull(out)
    }

    @Test
    fun filterLog_nullUser_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer(null, DateTime(500), DateTime(2000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNull(out)
    }

    @Test
    fun filterLog_nullStartTime_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", null, DateTime(2000))

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNull(out)
    }

    @Test
    fun filterLog_nullEndTime_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(500), null)

        // Act
        val out = filterer.filterLog(worklog)

        // Assert
        assertNull(out)
    }

    @Test
    fun filterLog_nullWorkLog_shouldReturnNull() {
        // Arrange
        val filterer = JiraLogFilterer("asdf", DateTime(500), DateTime(2000))

        // Act
        val out = filterer.filterLog(null)

        // Assert
        assertNull(out)
    }
}
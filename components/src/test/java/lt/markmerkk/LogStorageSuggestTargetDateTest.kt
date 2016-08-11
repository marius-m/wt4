package lt.markmerkk

import com.nhaarman.mockito_kotlin.mock
import lt.markmerkk.LogStorage
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-11
 */
class LogStorageSuggestTargetDateTest {

    val executor: IExecutor = mock()
    val storage = LogStorage(executor)

    @Test
    fun differentDay_changeTargetDate() {
        // Arrange
        val testDate = LogFormatters.longFormat.parseDateTime("2012-12-04 12:30")
        val newDate = LogFormatters.longFormat.parseDateTime("2012-12-05 11:30")
        storage.targetDate = testDate

        // Act
        storage.suggestTargetDate(LogFormatters.longFormat.print(newDate))

        // Assert
        val newDateWithoutTime = newDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
        assertEquals(newDateWithoutTime, storage.targetDate)
    }

    @Test
    fun sameDay_noTrigger() {
        // Arrange
        val testDate = LogFormatters.longFormat.parseDateTime("2012-12-05 12:30")
        val newDate = LogFormatters.longFormat.parseDateTime("2012-12-05 11:30")
        storage.targetDate = testDate

        // Act
        storage.suggestTargetDate(LogFormatters.longFormat.print(newDate))

        // Assert
        assertEquals(testDate, storage.targetDate)
    }

    @Test
    fun malformSuggestion_noTrigger() {
        // Arrange
        val testDate = LogFormatters.longFormat.parseDateTime("2012-12-05 12:30")
        storage.targetDate = testDate

        // Act
        storage.suggestTargetDate("malformed_suggestion")

        // Assert
        assertEquals(testDate, storage.targetDate)
    }
}
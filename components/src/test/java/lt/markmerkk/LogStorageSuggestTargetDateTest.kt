package lt.markmerkk

import com.nhaarman.mockito_kotlin.mock
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.utils.LogFormatters
import org.joda.time.DateTime
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-11
 */
class LogStorageSuggestTargetDateTest {

    val executor: IExecutor = mock()
    val storage = LogStorage(executor)


    @Before
    fun setUp() {
        storage.targetDate = DateTime(1000)
    }

    @Test
    fun differentDay_changeTargetDate() {
        // Arrange
        val newDate = LogFormatters.longFormat.parseDateTime("2012-12-05 11:30")

        // Act
        storage.suggestTargetDate(LogFormatters.longFormat.print(newDate))

        // Assert
        val newDateWithoutTime = newDate.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0)
        assertEquals(newDateWithoutTime, storage.targetDate)
    }

    @Test
    fun sameDay_noTrigger() {
        // Arrange
        val newDate = LogFormatters.longFormat.parseDateTime("2012-12-05 11:30")
        val oldDate = newDate.minus(3000).withTime(0, 0, 0, 0) // same day, different time
        storage.targetDate = oldDate

        // Act
        storage.suggestTargetDate(LogFormatters.longFormat.print(newDate))

        // Assert
        assertEquals(oldDate, storage.targetDate)
    }

    @Test
    fun malformSuggestion_noTrigger() {
        // Arrange
        val oldDate = storage.targetDate

        // Act
        storage.suggestTargetDate("malformed_suggestion")

        // Assert
        assertEquals(oldDate, storage.targetDate)
    }
}
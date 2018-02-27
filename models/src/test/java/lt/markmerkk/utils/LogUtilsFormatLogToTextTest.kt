package lt.markmerkk.utils

import lt.markmerkk.entities.SimpleLogBuilder
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class LogUtilsFormatLogToTextTest {

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        DateTimeUtils.setCurrentMillisFixed(1L)
    }

    @Test
    fun valid() {
        // Assemble
        val log = SimpleLogBuilder(DateTime.now().millis)
                .setStart(DateTime.now().millis)
                .setEnd(DateTime.now().plusMinutes(5).millis)
                .setTask("DEV-123")
                .setComment("valid_comment")
                .build()

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("DEV-123 (5m) valid_comment")
    }

    @Test
    fun noTask() {
        // Assemble
        val log = SimpleLogBuilder(DateTime.now().millis)
                .setStart(DateTime.now().millis)
                .setEnd(DateTime.now().plusMinutes(5).millis)
                .setComment("valid_comment")
                .build()

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("(5m) valid_comment")
    }

    @Test
    fun noComment() {
        // Assemble
        val log = SimpleLogBuilder(DateTime.now().millis)
                .setStart(DateTime.now().millis)
                .setEnd(DateTime.now().plusMinutes(5).millis)
                .setTask("DEV-123")
                .build()

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("DEV-123 (5m)")
    }

    @Test
    fun noTaskNoComment() {
        // Assemble
        val log = SimpleLogBuilder(DateTime.now().millis)
                .setStart(DateTime.now().millis)
                .setEnd(DateTime.now().plusMinutes(5).millis)
                .build()

        // Act
        val result = LogUtils.formatLogToText(log)

        // Assert
        assertThat(result).isEqualTo("(5m)")
    }

}
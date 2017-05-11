package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.mvp.MocksLogEditService.buildValidLog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-05-10
 */
class LogEditServiceImplTest {

    @Mock lateinit var listener: LogEditService.Listener
    @Mock lateinit var logEditInteractor: LogEditInteractor
    lateinit var service: LogEditServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun onAttach_populateData() {
        // Assemble
        val validLog = buildValidLog()
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doReturn(validLog).whenever(logEditInteractor).update(any(), any(), any())

        // Act
        service.onAttach()

        // Assert
        verify(listener).onDataChange(any(), any(), any(), any())
        verify(listener).onDurationChange(eq("30m"))
        verify(listener).onEnableSaving()
    }

    @Test
    fun updateTime_triggerCorrect() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = buildValidLog(fakeStart, fakeEnd)
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doReturn(validLog).whenever(logEditInteractor).update(any(), any(), any())

        // Act
        service.updateDateTime(
                LocalDate.from(fakeStart),
                LocalTime.from(fakeStart),
                LocalDate.from(fakeEnd),
                LocalTime.from(fakeEnd)
        )

        // Assert
        verify(listener).onDurationChange(eq("30m"))
        verify(listener).onEnableSaving()
    }

    @Test
    fun updateTime_failBuildingNewEntity_triggerCorrect() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = buildValidLog()
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doThrow(IllegalArgumentException()).whenever(logEditInteractor).update(any(), any(), any())

        // Act
        service.updateDateTime(
                LocalDate.from(fakeStart),
                LocalTime.from(fakeStart),
                LocalDate.from(fakeEnd),
                LocalTime.from(fakeEnd)
        )

        // Assert
        verify(listener).onDurationChange(eq("Invalid duration"))
        verify(listener).onDisableSaving()
    }

}
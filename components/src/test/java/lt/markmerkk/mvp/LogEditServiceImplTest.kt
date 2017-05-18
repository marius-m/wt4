package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.mvp.MocksLogEditService.buildValidLog
import lt.markmerkk.mvp.MocksLogEditService.mockValidLog
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
        doReturn(validLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.onAttach()

        // Assert
        verify(listener).onDataChange(any(), any(), any(), any())
        verify(listener).onDurationChange(eq("30m"))
        verify(listener).onGenericNotification(eq(""))
        verify(listener).onEnableInput() // Update time triggers it further
        verify(listener, atLeastOnce()).onEnableSaving()
    }

    @Test
    fun onAttach_whenInError_appendToDuration() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = mockValidLog(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment"
        )
        doReturn("30m.").whenever(validLog).prettyDuration
        doReturn(true).whenever(validLog).isError // worklog marked as having an error
        doReturn("valid_error_message").whenever(validLog).errorMessage
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doReturn(validLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.onAttach()

        // Assert
        verify(listener).onDurationChange(eq("30m."))
        verify(listener).onGenericNotification(eq("valid_error_message"))
        verify(listener).onEnableInput()
        verify(listener, atLeastOnce()).onEnableSaving() // update time triggers it further
    }

    @Test
    fun onAttach_whenAlreadyInSync_appendToDuration() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = mockValidLog(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment"
        )
        doReturn("30m.").whenever(validLog).prettyDuration
        doReturn(1234L).whenever(validLog).id // with having remote id this worklog is in sync
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doReturn(validLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.onAttach()

        // Assert
        verify(listener).onDurationChange(eq("30m."))
        verify(listener).onGenericNotification(eq("Worklog is already in sync with JIRA"))
        verify(listener).onDisableInput()
        verify(listener).onDisableSaving()
    }

    @Test
    fun updateTime_triggerCorrect() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = buildValidLog(fakeStart, fakeEnd)
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doReturn(validLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

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
        doThrow(IllegalArgumentException()).whenever(logEditInteractor)
                .updateDateTime(any(), any(), any())

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

    @Test
    fun saveValidEntity_triggerCorrect() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = buildValidLog()
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doReturn(validLog).whenever(logEditInteractor).updateTimeConvenience(
                any(),
                any(),
                any(),
                any(),
                any()
        )

        // Act
        service.saveEntity(
                LocalDate.from(fakeStart),
                LocalTime.from(fakeStart),
                LocalDate.from(fakeEnd),
                LocalTime.from(fakeEnd),
                "valid_ticket",
                "valid_comment"
        )

        // Assert
        verify(listener).onEntitySaveComplete()
    }

    @Test
    fun saveEntity_invalidEntity_triggerCorrect() {
        // Assemble
        val fakeStart = LocalDateTime.of(2014, 1, 12, 12, 30, 0)
        val fakeEnd = LocalDateTime.of(2014, 1, 12, 13, 0, 0)
        val validLog = buildValidLog()
        service = LogEditServiceImpl(logEditInteractor, listener, validLog)
        doThrow(IllegalArgumentException()).whenever(logEditInteractor).updateTimeConvenience(
                any(),
                any(),
                any(),
                any(),
                any()
        )

        // Act
        service.saveEntity(
                LocalDate.from(fakeStart),
                LocalTime.from(fakeStart),
                LocalDate.from(fakeEnd),
                LocalTime.from(fakeEnd),
                "valid_ticket",
                "valid_comment"
        )

        // Assert
        verify(listener, never()).onEntitySaveComplete()
        verify(listener).onEntitySaveFail(any())
    }

}
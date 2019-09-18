package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.TimeProviderTest
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

class LogEditServiceImplTest {

    @Mock lateinit var listener: LogEditService.Listener
    @Mock lateinit var logEditInteractor: LogEditInteractor
    lateinit var service: LogEditServiceImpl

    private val timeProvider = TimeProviderTest()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = LogEditServiceImpl(
                logEditInteractor,
                timeProvider,
                listener
        )
    }

    @Test
    fun populateData() {
        // Assemble
        val simpleLog = MocksLogEditService.createValidLogWithDate()
        doReturn(simpleLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.entityInEdit = simpleLog
        service.redraw()

        // Assert
        verify(listener).onDataChange(any(), any())
        verify(listener).onDurationChange(eq("30m"))
        verify(listener).onGenericNotification(eq(""))
        verify(listener).onEnableInput() // Update time triggers it further
        verify(listener, atLeastOnce()).onEnableSaving()
    }

    @Test
    fun whenInError_appendToDuration() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.mockValidLogWith(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment",
                ""
        )
        doReturn("30m.").whenever(simpleLog).prettyDuration
        doReturn(true).whenever(simpleLog).canEdit()
        doReturn(true).whenever(simpleLog).isError // worklog marked as having an error
        doReturn("valid_error_message").whenever(simpleLog).errorMessage
        doReturn(simpleLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.entityInEdit = simpleLog
        service.redraw()

        // Assert
        verify(listener).onDurationChange(eq("30m."))
        verify(listener).onGenericNotification(eq(""))
        verify(listener).onEnableInput()
        verify(listener, atLeastOnce()).onEnableSaving() // update time triggers it further
    }

    @Test
    fun whenAlreadyInSync_appendToDuration() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.mockValidLogWith(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment",
                ""
        )
        doReturn("30m.").whenever(simpleLog).prettyDuration
        doReturn(true).whenever(simpleLog).isRemote
        doReturn(simpleLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.entityInEdit = simpleLog
        service.redraw()

        // Assert
        verify(listener).onDurationChange(eq("30m."))
        verify(listener).onGenericNotification(eq(""))
        verify(listener).onDisableInput()
        verify(listener).onDisableSaving()
    }

    @Test
    fun updateTime() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.createValidLogWithDate(fakeStart, fakeEnd)
        doReturn(simpleLog).whenever(logEditInteractor).updateDateTime(any(), any(), any())

        // Act
        service.updateDateTime(
                fakeStart,
                fakeEnd
        )

        // Assert
        verify(listener).onDurationChange(eq("30m"))
        verify(listener).onEnableSaving()
    }

    @Test
    fun updateTime_failBuildingNewEntity() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.createValidLogWithDate()
        doThrow(IllegalArgumentException()).whenever(logEditInteractor)
                .updateDateTime(any(), any(), any())

        // Act
        service.entityInEdit = simpleLog
        service.updateDateTime(
                fakeStart,
                fakeEnd
        )

        // Assert
        verify(listener).onDurationChange(eq("Invalid duration"))
        verify(listener).onDisableSaving()
    }

    @Test
    fun validUpdate() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.createValidLogWithDate()
        doReturn(simpleLog).whenever(logEditInteractor).updateTimeConvenience(
                any(),
                any(),
                any(),
                any(),
                any()
        )
        service.serviceType = LogEditService.ServiceType.UPDATE

        // Act
        service.entityInEdit = simpleLog
        service.saveEntity(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment"
        )

        // Assert
        verify(logEditInteractor).update(any())
        verify(listener).onEntitySaveComplete()
    }

    @Test
    fun validInsert() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.createValidLogWithDate()
        doReturn(simpleLog).whenever(logEditInteractor).updateTimeConvenience(
                any(),
                any(),
                any(),
                any(),
                any()
        )
        service.serviceType = LogEditService.ServiceType.CREATE

        // Act
        service.entityInEdit = simpleLog
        service.saveEntity(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment"
        )

        // Assert
        verify(logEditInteractor).create(any())
        verify(listener).onEntitySaveComplete()
    }

    @Test
    fun invalidEntity_onSave() {
        // Assemble
        val fakeStart = DateTime(2014, 1, 12, 12, 30, 0)
        val fakeEnd = DateTime(2014, 1, 12, 13, 0, 0)
        val simpleLog = MocksLogEditService.createValidLogWithDate()
        doThrow(IllegalArgumentException()).whenever(logEditInteractor).updateTimeConvenience(
                any(),
                any(),
                any(),
                any(),
                any()
        )

        // Act
        service.entityInEdit = simpleLog
        service.saveEntity(
                fakeStart,
                fakeEnd,
                "valid_ticket",
                "valid_comment"
        )

        // Assert
        verify(listener).onEntitySaveFail(any())
    }

}
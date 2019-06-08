package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.IDataStorage
import lt.markmerkk.Mocks
import lt.markmerkk.entities.SimpleLog
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-09-09
 */
class LogStatusServiceImplTest {

    @Mock lateinit var listener: LogStatusService.Listener
    @Mock lateinit var logStorage: IDataStorage<SimpleLog>

    lateinit var presenter: LogStatusServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = LogStatusServiceImpl(
                listener,
                Schedulers.immediate(),
                Schedulers.immediate(),
                logStorage
        )
    }

    @Test
    fun validLog() {
        // Assemble
        val validId = 1L
        val validLog = MocksLogEditService.mockValidLogWith(
                "valid_task",
                "valid_message"
        )
        doReturn(validLog).whenever(logStorage).findByIdOrNull(validId)

        // Act
        presenter.showWithId(validId)

        // Assert
        verify(listener).show(any(), any())
    }

    @Test
    fun nullId() {
        // Assemble
        // Act
        presenter.showWithId(null)

        // Assert
        verify(listener).hide()
    }

    @Test
    fun validLog_displayCalledTwice_showOnce() {
        // Assemble
        val validId = 1L
        val validLog = MocksLogEditService.mockValidLogWith(
                "valid_task",
                "valid_message"
        )
        doReturn(validLog).whenever(logStorage).findByIdOrNull(validId)

        // Act
        presenter.showWithId(validId)
        presenter.showWithId(validId)

        // Assert
        verify(listener, times(1)).show(any(), any())
    }

    @Test
    fun logMissing() {
        // Assemble
        val logId = 1L
        doReturn(null).whenever(logStorage).findByIdOrNull(logId)

        // Act
        presenter.showWithId(logId)

        // Assert
        verify(listener).hide()
    }
}
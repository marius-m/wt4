package lt.markmerkk.mvp

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.IDataStorage
import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.mvp.MocksLogEditService.mockValidLogWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-09-10
 */
class LogStatusServiceImplToHeaderTextTest {
    @Mock lateinit var listener: LogStatusService.Listener
    @Mock lateinit var logStorage: IDataStorage<SimpleLog>

    val durationInMinutes = 1000000L // ~16 minutes
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
        val validLog = mockValidLogWith(
                "valid_title",
                "valid_message"
        )
        doReturn(durationInMinutes).whenever(validLog).duration

        // Act
        val result = presenter.toHeaderText(validLog)

        // Assert
        assertEquals("valid_title (16m)", result)
    }

    @Test
    fun noTask() {
        // Assemble
        val validLog = mockValidLogWith(
                "",
                "valid_message"
        )
        doReturn(durationInMinutes).whenever(validLog).duration

        // Act
        val result = presenter.toHeaderText(validLog)

        // Assert
        assertEquals("(16m)", result)
    }

}
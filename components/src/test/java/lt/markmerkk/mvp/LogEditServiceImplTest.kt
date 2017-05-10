package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import lt.markmerkk.entities.SimpleLog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-05-10
 */
class LogEditServiceImplTest {

    @Mock lateinit var listener: LogEditService.Listener
    @Mock lateinit var fakeLog: SimpleLog
    lateinit var service: LogEditServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = LogEditServiceImpl(listener, fakeLog)
    }

    @Test
    fun onAttach_populateData() {
        // Act
        service.onAttach()

        // Assert
        verify(listener).onEntityChange(eq(fakeLog))
    }
}
package lt.markmerkk.mvp

import lt.markmerkk.TimeProviderTest
import lt.markmerkk.utils.hourglass.HourGlass
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-05-14
 */
abstract class AbsClockEditPresenterImplTest {

    @Mock lateinit var hourglass: HourGlass
    @Mock lateinit var view: ClockEditMVP.View
    lateinit var presenter: ClockEditPresenterImpl

    @Before
    open fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = ClockEditPresenterImpl(view, hourglass, TimeProviderTest())
    }

}
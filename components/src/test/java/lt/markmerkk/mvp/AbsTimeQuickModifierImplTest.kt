package lt.markmerkk.mvp

import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations

abstract class AbsTimeQuickModifierImplTest {

    @Mock lateinit var listener: TimeQuickModifier.Listener
    lateinit var modifier: TimeQuickModifierImpl

    @Before
    open fun setUp() {
        MockitoAnnotations.initMocks(this)
        modifier = TimeQuickModifierImpl(listener)
    }
}
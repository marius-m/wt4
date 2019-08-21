package lt.markmerkk.interactors

import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler

abstract class AbsAuthServiceImplTest {

    @Mock lateinit var view: AuthService.View
    @Mock lateinit var authInteractor: AuthService.AuthInteractor
    lateinit var service: AuthServiceImpl
    lateinit var serviceWithTestSchedulers: AuthServiceImpl

    val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = AuthServiceImpl(
                view,
                Schedulers.immediate(),
                Schedulers.immediate(),
                authInteractor
        )
        serviceWithTestSchedulers = AuthServiceImpl(
                view,
                testScheduler,
                testScheduler,
                authInteractor
        )
    }

}
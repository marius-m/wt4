package lt.markmerkk.interactors

import lt.markmerkk.interactors.AuthService
import lt.markmerkk.interactors.AuthServiceImpl
import lt.markmerkk.interactors.LogLoader
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler

abstract class AbsAuthServiceImplTest {

    @Mock lateinit var view: AuthService.View
    @Mock lateinit var authInteractor: AuthService.AuthInteractor
    @Mock lateinit var logLoader: LogLoader
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
                authInteractor,
                logLoader
        )
        serviceWithTestSchedulers = AuthServiceImpl(
                view,
                testScheduler,
                testScheduler,
                authInteractor,
                logLoader
        )
    }

}
package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.*
import lt.markmerkk.JiraInteractor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.schedulers.Schedulers

/**
 * @author mariusmerkevicius
 * *
 * @since 2017-09-16
 */
class AuthServiceImplTestLoginTest {

    @Mock lateinit var view: AuthService.View
    @Mock lateinit var authInteractor: AuthService.AuthInteractor
    lateinit var service: AuthService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = AuthServiceImpl(
                view,
                Schedulers.immediate(),
                Schedulers.immediate(),
                authInteractor
        )
    }

    @Test
    fun showProgressWhenLoading() {
        // Assemble
        doReturn(Observable.just(true)).whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

        // Act
        service.testLogin(
                "valid_host",
                "valid_username",
                "valid_password"
        )

        // Assert
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    @Test
    fun validLogin() {
        // Assemble
        doReturn(Observable.just(true)).whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

        // Act
        service.testLogin(
                "valid_host",
                "valid_username",
                "valid_password"
        )

        // Assert
        val resultCaptor = argumentCaptor<AuthService.AuthResult>()
        verify(view).showAuthResult(resultCaptor.capture())
        assertEquals(AuthService.AuthResult.SUCCESS, resultCaptor.firstValue)
    }

    @Test
    fun noInputValues() {
        // Assemble
        doReturn(Observable.error<Any>(IllegalArgumentException("empty hostname")))
                .whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

        // Act
        service.testLogin(
                "",
                "",
                ""
        )

        // Assert
        val resultCaptor = argumentCaptor<AuthService.AuthResult>()
        verify(view).showAuthResult(resultCaptor.capture())
        assertEquals(AuthService.AuthResult.ERROR_EMPTY_FIELDS, resultCaptor.firstValue)
    }

    @Test
    fun validInvalidUnauthorised() {
        // Assemble
        doReturn(Observable.error<Any>(RuntimeException("Error: 401 Unauthorized")))
                .whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

        // Act
        service.testLogin(
                "valid_host",
                "valid_username",
                "valid_password"
        )

        // Assert
        val resultCaptor = argumentCaptor<AuthService.AuthResult>()
        verify(view).showAuthResult(resultCaptor.capture())
        assertEquals(AuthService.AuthResult.ERROR_UNAUTHORISED, resultCaptor.firstValue)
    }

    @Test
    fun validInvalidHostname() {
        // Assemble
        doReturn(Observable.error<Any>(RuntimeException("Error: 404 Not Found")))
                .whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

        // Act
        service.testLogin(
                "valid_host",
                "valid_username",
                "valid_password"
        )

        // Assert
        val resultCaptor = argumentCaptor<AuthService.AuthResult>()
        verify(view).showAuthResult(resultCaptor.capture())
        assertEquals(AuthService.AuthResult.ERROR_INVALID_HOSTNAME, resultCaptor.firstValue)
    }

    @Test
    fun validInvalidUndefined() {
        // Assemble
        doReturn(Observable.error<Any>(RuntimeException("Error: Undefined error!")))
                .whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

        // Act
        service.testLogin(
                "valid_host",
                "valid_username",
                "valid_password"
        )

        // Assert
        val resultCaptor = argumentCaptor<AuthService.AuthResult>()
        verify(view).showAuthResult(resultCaptor.capture())
        assertEquals(AuthService.AuthResult.ERROR_UNDEFINED, resultCaptor.firstValue)
    }
}
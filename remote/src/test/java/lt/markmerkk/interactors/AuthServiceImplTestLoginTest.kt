package lt.markmerkk.interactors

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraMocks
import lt.markmerkk.UserSettings
import net.rcarz.jiraclient.RestException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler

class AuthServiceImplTestLoginTest {

    @Mock lateinit var view: AuthService.View
    @Mock lateinit var authInteractor: AuthService.AuthInteractor
    @Mock lateinit var userSettings: UserSettings
    lateinit var service: AuthServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        service = AuthServiceImpl(
                view,
                Schedulers.immediate(),
                Schedulers.immediate(),
                authInteractor,
                userSettings
        )
    }

    @Test
    fun showProgressWhenLoading() {
        // Assemble
        doReturn(Single.just(JiraMocks.createJiraUser()))
                .whenever(authInteractor).jiraTestValidConnection(any(), any(), any())

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
        doReturn(Single.just(JiraMocks.createJiraUser()))
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
        assertEquals(AuthService.AuthResult.SUCCESS, resultCaptor.firstValue)
        verify(userSettings).changeJiraUser(any(), any(), any(), any())
    }

    @Test
    fun noInputValues() {
        // Assemble
        doReturn(Single.error<Any>(IllegalArgumentException("empty hostname")))
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
        verify(userSettings).resetUserData()
    }

    @Test
    fun validInvalidUnauthorised() {
        // Assemble
        val restException = RestException(
                "message",
                401,
                "hostname_not_found",
                emptyArray()
        )
        doReturn(Single.error<Any>(RuntimeException(restException)))
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
        verify(userSettings).resetUserData()
    }

    @Test
    fun validInvalidHostname() {
        // Assemble
        val restException = RestException(
                "message",
                404,
                "hostname_not_found",
                emptyArray()
        )
        doReturn(Single.error<Any>(RuntimeException(restException)))
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
        verify(userSettings).resetUserData()
    }

    @Test
    fun validInvalidUndefined() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
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
        verify(userSettings).resetUserData()
    }
}
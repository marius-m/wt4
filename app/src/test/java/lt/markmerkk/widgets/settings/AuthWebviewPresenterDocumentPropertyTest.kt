package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Observable
import java.lang.RuntimeException

class AuthWebviewPresenterDocumentPropertyTest {

    @Mock lateinit var view: AuthWebviewPresenter.View
    @Mock lateinit var authResultParser: AuthResultParser
    lateinit var authWebviewPresenter: AuthWebviewPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        authWebviewPresenter = AuthWebviewPresenter(
                view,
                authResultParser
        )
    }

    @Test
    fun valid() {
        // Assemble
        doReturn("code").whenever(authResultParser).findAccessToken(any())
        val documentContentStream = Observable.just(AuthWebviewPresenter.DocumentContent(
                documentUri = "https://localhost/${AuthWebviewPresenter.AUTH_URI}",
                documentContent = "valid_verification_code"
        ))

        // Act
        authWebviewPresenter.attachDocumentProperty(documentContentStream)

        // Assert
        verify(view).onAccessToken("code")
    }

    @Test
    fun randomUri() {
        // Assemble
        doReturn("").whenever(authResultParser).findAccessToken(any())
        val documentContentStream = Observable.just(AuthWebviewPresenter.DocumentContent(
                documentUri = "https://localhost/randomUri",
                documentContent = "content"
        ))

        // Act
        authWebviewPresenter.attachDocumentProperty(documentContentStream)

        // Assert
        verify(view, never()).onAccessToken(any())
    }

    @Test
    fun cantFindCode() {
        // Assemble
        doReturn("").whenever(authResultParser).findAccessToken(any())
        val documentContentStream = Observable.just(AuthWebviewPresenter.DocumentContent(
                documentUri = "https://localhost/${AuthWebviewPresenter.AUTH_URI}",
                documentContent = "valid_verification_code"
        ))

        // Act
        authWebviewPresenter.attachDocumentProperty(documentContentStream)

        // Assert
        verify(view).onAccessToken("")
    }

    @Test
    fun errorResolvingCode() {
        // Assemble
        doThrow(RuntimeException()).whenever(authResultParser).findAccessToken(any())
        val documentContentStream = Observable.just(AuthWebviewPresenter.DocumentContent(
                documentUri = "https://localhost/${AuthWebviewPresenter.AUTH_URI}",
                documentContent = "valid_verification_code"
        ))

        // Act
        authWebviewPresenter.attachDocumentProperty(documentContentStream)

        // Assert
        verify(view).onAccessToken("")
    }
}
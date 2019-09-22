package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraOAuthCreds
import lt.markmerkk.JiraUser
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.JiraBasicApi
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class OAuthAuthorizatorSetupAuth1Test {

    @Mock lateinit var view: OAuthAuthorizator.View
    @Mock lateinit var oauthInteractor: OAuthInteractor
    @Mock lateinit var jiraClientProvider: JiraClientProvider
    @Mock lateinit var jiraBasicApi: JiraBasicApi
    @Mock lateinit var userSettings: UserSettings
    lateinit var authorizator: OAuthAuthorizator

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        authorizator = OAuthAuthorizator(
                view,
                oauthInteractor,
                jiraClientProvider,
                jiraBasicApi,
                userSettings,
                Schedulers.immediate(),
                Schedulers.immediate()
        )
    }

    @Test
    fun validAuthUrl() {
        // Assemble
        doReturn(Single.just("auth_url")).whenever(oauthInteractor).generateAuthUrl()

        // Act
        authorizator.setupAuthStep1()

        // Assert
        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view).renderView(viewModelCapture.capture())
        val viewModel = viewModelCapture.firstValue
        assertThat(viewModel.showContainerWebview).isTrue()
        assertThat(viewModel.showContainerStatus).isFalse()
        assertThat(viewModel.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.NEUTRAL)
        assertThat(viewModel.textStatus).isEqualTo("")
        assertThat(viewModel.showButtonSetupNew).isFalse()

        verify(userSettings).resetUserData()
        verify(view).loadAuthWeb("auth_url")
    }

    @Test
    fun errorExportingToken() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException())).whenever(oauthInteractor).generateAuthUrl()

        // Act
        authorizator.setupAuthStep1()

        // Assert
        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view).renderView(viewModelCapture.capture())
        val viewModel = viewModelCapture.firstValue
        assertThat(viewModel.showContainerWebview).isFalse()
        assertThat(viewModel.showContainerStatus).isTrue()
        assertThat(viewModel.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.SAD)
        assertThat(viewModel.textStatus).isEqualTo("Error generating JIRA token. Press 'Show logs' for more info!")
        assertThat(viewModel.showButtonSetupNew).isTrue()

        verify(view, never()).loadAuthWeb("auth_url")
    }
}
package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Mocks
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
import java.lang.Appendable
import java.lang.RuntimeException

class OAuthAuthorizatorCheckAuthTest {

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
    fun validConnection() {
        // Assemble
        doReturn(Single.just(Mocks.createUser())).whenever(jiraBasicApi).jiraUser()

        // Act
        authorizator.checkAuth()

        // Assert
        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view).renderView(viewModelCapture.capture())
        val viewModel = viewModelCapture.firstValue
        assertThat(viewModel.showContainerWebview).isFalse()
        assertThat(viewModel.showContainerStatus).isTrue()
        assertThat(viewModel.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.HAPPY)
        assertThat(viewModel.textStatus).isEqualTo("Welcome 'valid_display_name'!")
        assertThat(viewModel.showButtonSetupNew).isFalse()

        verify(userSettings).changeJiraUser(any(), any(), any())
        verify(view).showProgress()
        verify(view).hideProgress()
    }

    @Test
    fun errorGettingClient() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException())).whenever(jiraBasicApi).jiraUser()

        // Act
        authorizator.checkAuth()

        // Assert
        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view).renderView(viewModelCapture.capture())
        val viewModel = viewModelCapture.firstValue
        assertThat(viewModel.showContainerWebview).isFalse()
        assertThat(viewModel.showContainerStatus).isTrue()
        assertThat(viewModel.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.SAD)
        assertThat(viewModel.textStatus).isEqualTo("Error connecting to JIRA. Press 'Show logs' for more details!")
        assertThat(viewModel.showButtonSetupNew).isTrue()

        verify(userSettings).resetUserData()
    }
}
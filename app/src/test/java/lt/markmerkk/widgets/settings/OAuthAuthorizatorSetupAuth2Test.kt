package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraOAuthCreds
import lt.markmerkk.JiraUser
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.JiraBasicApi
import net.rcarz.jiraclient.JiraClient
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers
import java.lang.RuntimeException

class OAuthAuthorizatorSetupAuth2Test {

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
    fun validToken() {
        // Assemble
        doReturn(Single.just(OAuthInteractor.OAuthToken("token", "key")))
                .whenever(oauthInteractor).generateToken(any())
        doReturn(Single.just(JiraUser("name", "display_name", "email")))
                .whenever(jiraBasicApi).jiraUser()
        doReturn(mockJiraClient()).whenever(jiraClientProvider).newClient()

        // Act
        authorizator.setupAuthStep2("valid_token")

        // Assert
        verify(userSettings).changeOAuthCreds(tokenSecret = "token", accessKey = "key")
        verify(userSettings).changeJiraUser(name = "name", email = "email", displayName = "display_name")

        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view, times(2)).renderView(viewModelCapture.capture())
        val viewModel1 = viewModelCapture.firstValue
        assertThat(viewModel1.showContainerWebview).isFalse()
        assertThat(viewModel1.showContainerStatus).isTrue()
        assertThat(viewModel1.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.NEUTRAL)
        assertThat(viewModel1.textStatus).isEqualTo("Finishing up authorization...")
        assertThat(viewModel1.showButtonSetupNew).isFalse()

        val viewModel2 = viewModelCapture.allValues[1]
        assertThat(viewModel2.showContainerWebview).isFalse()
        assertThat(viewModel2.showContainerStatus).isTrue()
        assertThat(viewModel2.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.HAPPY)
        assertThat(viewModel2.textStatus).isEqualTo("Welcome 'display_name'!")
        assertThat(viewModel2.showButtonSetupNew).isFalse()

        verify(view).accountReady()
    }

    @Test
    fun noToken() {
        // Assemble
        // Act
        authorizator.setupAuthStep2("")

        // Assert
        verify(view).resetWeb()
        verify(userSettings).resetUserData()

        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view, times(2)).renderView(viewModelCapture.capture())

        val viewModel1 = viewModelCapture.firstValue
        assertThat(viewModel1.showContainerWebview).isFalse()
        assertThat(viewModel1.showContainerStatus).isTrue()
        assertThat(viewModel1.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.NEUTRAL)
        assertThat(viewModel1.textStatus).isEqualTo("Finishing up authorization...")
        assertThat(viewModel1.showButtonSetupNew).isFalse()

        val viewModel2 = viewModelCapture.allValues[1]
        assertThat(viewModel2.showContainerWebview).isFalse()
        assertThat(viewModel2.showContainerStatus).isTrue()
        assertThat(viewModel2.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.SAD)
        assertThat(viewModel2.textStatus).isEqualTo("Error generating JIRA token. Press 'Show logs' for more info")
        assertThat(viewModel2.showButtonSetupNew).isTrue()

        verify(view, never()).accountReady()
    }

    @Test
    fun errorGettingToken() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(oauthInteractor).generateToken(any())
        doReturn(Single.just(JiraUser("name", "display_name", "email")))
                .whenever(jiraBasicApi).jiraUser()
        doReturn(mockJiraClient()).whenever(jiraClientProvider).newClient()

        // Act
        authorizator.setupAuthStep2("valid_token")

        // Assert
        verify(view).resetWeb()
        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view, times(2)).renderView(viewModelCapture.capture())

        val viewModel1 = viewModelCapture.firstValue
        assertThat(viewModel1.showContainerWebview).isFalse()
        assertThat(viewModel1.showContainerStatus).isTrue()
        assertThat(viewModel1.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.NEUTRAL)
        assertThat(viewModel1.textStatus).isEqualTo("Finishing up authorization...")
        assertThat(viewModel1.showButtonSetupNew).isFalse()

        val viewModel2 = viewModelCapture.allValues[1]
        assertThat(viewModel2.showContainerWebview).isFalse()
        assertThat(viewModel2.showContainerStatus).isTrue()
        assertThat(viewModel2.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.SAD)
        assertThat(viewModel2.textStatus).isEqualTo("Error generating JIRA token. Press 'Show logs' for more info")
        assertThat(viewModel2.showButtonSetupNew).isTrue()

        verify(view, never()).accountReady()
    }

    @Test
    fun errorGettingNewUser() {
        // Assemble
        doReturn(Single.just(OAuthInteractor.OAuthToken("token", "key")))
                .whenever(oauthInteractor).generateToken(any())
        doReturn(Single.error<Any>(RuntimeException()))
                .whenever(jiraBasicApi).jiraUser()
        doReturn(mockJiraClient()).whenever(jiraClientProvider).newClient()

        // Act
        authorizator.setupAuthStep2("valid_token")

        // Assert
        verify(view).resetWeb()
        verify(userSettings).resetUserData()

        val viewModelCapture = argumentCaptor<AuthViewModel>()
        verify(view, times(2)).renderView(viewModelCapture.capture())
        val viewModel1 = viewModelCapture.firstValue
        assertThat(viewModel1.showContainerWebview).isFalse()
        assertThat(viewModel1.showContainerStatus).isTrue()
        assertThat(viewModel1.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.NEUTRAL)
        assertThat(viewModel1.textStatus).isEqualTo("Finishing up authorization...")
        assertThat(viewModel1.showButtonSetupNew).isFalse()

        val viewModel2 = viewModelCapture.allValues[1]
        assertThat(viewModel2.showContainerWebview).isFalse()
        assertThat(viewModel2.showContainerStatus).isTrue()
        assertThat(viewModel2.showStatusEmoticon).isEqualTo(AuthViewModel.StatusEmoticon.SAD)
        assertThat(viewModel2.textStatus).isEqualTo("Error generating JIRA token. Press 'Show logs' for more info")
        assertThat(viewModel2.showButtonSetupNew).isTrue()

        verify(view, never()).accountReady()
    }

    //region Mocks

    fun mockJiraClient(): JiraClient {
        return mock()
    }

    //endregion

}
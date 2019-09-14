package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.*
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraOAuthCreds
import lt.markmerkk.JiraUser
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.JiraBasicApi
import net.rcarz.jiraclient.JiraClient
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
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                textStatus = "Finishing up authorization..."
        ))
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.HAPPY,
                textStatus = "Success setting up new user 'display_name'!"
        ))
    }

    @Test
    fun noToken() {
        // Assemble
        // Act
        authorizator.setupAuthStep2("")

        // Assert
        verify(view).resetWeb()
        verify(userSettings).resetUserData()
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                textStatus = "Error generating JIRA token. Try again later or press 'Show logs' for more info"
        ))
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
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                textStatus = "Finishing up authorization..."
        ))
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                textStatus = "Error generating JIRA token. Try again later or press 'Show logs' for more info"
        ))
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
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                textStatus = "Finishing up authorization..."
        ))
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                textStatus = "Error generating JIRA token. Try again later or press 'Show logs' for more info"
        ))
    }

    //region Mocks

    fun mockJiraClient(): JiraClient {
        return mock()
    }

    //endregion

}
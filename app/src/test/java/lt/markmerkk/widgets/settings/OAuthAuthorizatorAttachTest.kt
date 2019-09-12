package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.*
import lt.markmerkk.interactors.JiraBasicApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.Single
import rx.schedulers.Schedulers

class OAuthAuthorizatorAttachTest {

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
    fun noUserData() {
        // Assemble
        val user = JiraUser(name = "", displayName = "", email = "")
        val token = JiraOAuthCreds(tokenSecret = "token", accessKey = "key")
        doReturn(user).whenever(userSettings).jiraUser()
        doReturn(token).whenever(userSettings).jiraOAuthCreds()

        // Act
        authorizator.onAttach()

        // Assert
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                showButtonSetUp = true,
                textHeader = "",
                textStatus = "No user connected! Press 'Set-up' to setup your connection!"
        ))
    }

    @Test
    fun noTokenData() {
        // Assemble
        val user = JiraUser(name = "user", displayName = "user", email = "email")
        val token = JiraOAuthCreds(tokenSecret = "", accessKey = "")
        doReturn(user).whenever(userSettings).jiraUser()
        doReturn(token).whenever(userSettings).jiraOAuthCreds()

        // Act
        authorizator.onAttach()

        // Assert
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                showButtonSetUp = true,
                textHeader = "",
                textStatus = "No user connected! Press 'Set-up' to setup your connection!"
        ))
    }

    @Test
    fun userAvailable() {
        // Assemble
        val user = JiraUser(name = "user", displayName = "user", email = "email")
        val token = JiraOAuthCreds(tokenSecret = "token", accessKey = "key")
        doReturn(user).whenever(userSettings).jiraUser()
        doReturn(token).whenever(userSettings).jiraOAuthCreds()

        // Act
        authorizator.onAttach()

        // Assert
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                showButtonSetUp = true,
                textHeader = "",
                textStatus = "User 'username' connected! Press on face to check connection status or 'Set-up' for establishing new connection"
        ))
    }

}
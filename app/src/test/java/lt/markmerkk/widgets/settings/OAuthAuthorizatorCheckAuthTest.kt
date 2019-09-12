package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.Mocks
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.JiraBasicApi
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
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.HAPPY,
                showButtonSetUp = true,
                textHeader = "",
                textStatus = "Successfully connected to JIRA with user 'valid_display_name'! Press 'Set-up' to setup a new connection!"
        ))
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
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                showButtonSetUp = true,
                textHeader = "",
                textStatus = "Error connecting to JIRA. Press 'Set-up' to establish new connection"
        ))
        verify(userSettings).resetUserData()
    }
}
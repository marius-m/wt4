package lt.markmerkk.widgets.settings

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.JiraClientProvider
import lt.markmerkk.JiraOAuthCreds
import lt.markmerkk.JiraUser
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.JiraBasicApi
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
        verify(userSettings).resetUserData()
        verify(view).renderView(AuthViewModel(
                showContainerWebview = true,
                showContainerStatus = false,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.NEUTRAL,
                textStatus = ""
        ))
        verify(view).loadAuthWeb("auth_url")
    }

    @Test
    fun errorExportingToken() {
        // Assemble
        doReturn(Single.error<Any>(RuntimeException())).whenever(oauthInteractor).generateAuthUrl()

        // Act
        authorizator.setupAuthStep1()

        // Assert
        verify(view).renderView(AuthViewModel(
                showContainerWebview = false,
                showContainerStatus = true,
                showStatusEmoticon = AuthViewModel.StatusEmoticon.SAD,
                textStatus = "Error generating JIRA token. Try again later or press 'Show logs' for more info"
        ))
        verify(view, never()).loadAuthWeb("auth_url")
    }
}
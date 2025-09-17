package lt.markmerkk

import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.exceptions.AuthException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraClientProvider2ClientTest {

    @Mock lateinit var userSettings: UserSettings
    lateinit var jiraClientProvider: JiraClientProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraClientProvider = JiraClientProviderBasic(userSettings)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds()).whenever(userSettings).jiraBasicCreds()

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test(expected = AuthException::class)
    fun hasError() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds()).whenever(userSettings).jiraBasicCreds()

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertThat(result).isNotNull()

        // Act
        jiraClientProvider.markAsError()
        jiraClientProvider.client() // error client
    }

    @Test
    fun sameClient() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds()).whenever(userSettings).jiraBasicCreds()

        // Act
        val result1 = jiraClientProvider.client()

        // Assert
        assertThat(result1).isNotNull()

        // Act
        val result2 = jiraClientProvider.client()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isEqualTo(result2)
    }

    @Test
    fun differentCredentials() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds()).whenever(userSettings).jiraBasicCreds()

        // Act
        val result1 = jiraClientProvider.client()

        // Assert
        assertThat(result1).isNotNull()

        // Assemble
        doReturn(Mocks.createJiraBasicCreds("host2", "user2", "pass2"))
                .whenever(userSettings).jiraBasicCreds()

        // Act
        val result2 = jiraClientProvider.client()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }

    @Test(expected = AuthException::class)
    fun invalidHost() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "",
                username = "user2",
                password = "pass2"
        )).whenever(userSettings).jiraBasicCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidUsername() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host2",
                username = "",
                password = "pass2"
        )).whenever(userSettings).jiraBasicCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidPass() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host2",
                username = "user2",
                password = ""
        )).whenever(userSettings).jiraBasicCreds()

        // Act
        // Assert
        jiraClientProvider.client()
    }

}
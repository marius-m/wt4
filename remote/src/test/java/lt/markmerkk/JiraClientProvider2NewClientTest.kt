package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraClientProvider2NewClientTest {

    @Mock
    lateinit var userSettings: UserSettings
    lateinit var jiraClientProvider: JiraClientProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraClientProvider = JiraClientProviderBasic(userSettings)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host",
                username = "user",
                password = "pass"
        )).whenever(userSettings).jiraBasicCreds()

        // Act
        val result = jiraClientProvider.newClient()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun sameClient() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host",
                username = "user",
                password = "pass"
        )).whenever(userSettings).jiraBasicCreds()

        // Act
        val result1 = jiraClientProvider.newClient()

        // Assert
        assertThat(result1).isNotNull()

        // Act
        val result2 = jiraClientProvider.newClient()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }

    @Test
    fun markAsError() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host",
                username = "user",
                password = "pass"
        )).whenever(userSettings).jiraBasicCreds()

        // Act
        val result1 = jiraClientProvider.newClient()

        // Assert
        assertThat(result1).isNotNull()

        // Act
        jiraClientProvider.markAsError()
        val result2 = jiraClientProvider.newClient()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }
}
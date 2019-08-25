package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.exceptions.AuthException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraClientProvider2ClientTest {

    @Mock lateinit var userSettings: UserSettings
    lateinit var jiraClientProvider: JiraClientProvider2

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraClientProvider = JiraClientProvider2(userSettings)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn("host").whenever(userSettings).host
        doReturn("user").whenever(userSettings).username
        doReturn("pass").whenever(userSettings).password

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test(expected = AuthException::class)
    fun hasError() {
        // Assemble
        doReturn("host").whenever(userSettings).host
        doReturn("user").whenever(userSettings).username
        doReturn("pass").whenever(userSettings).password

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
        doReturn("host").whenever(userSettings).host
        doReturn("user").whenever(userSettings).username
        doReturn("pass").whenever(userSettings).password

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
        doReturn("host").whenever(userSettings).host
        doReturn("user").whenever(userSettings).username
        doReturn("pass").whenever(userSettings).password

        // Act
        val result1 = jiraClientProvider.client()

        // Assert
        assertThat(result1).isNotNull()

        // Assemble
        doReturn("host2").whenever(userSettings).host
        doReturn("user2").whenever(userSettings).username
        doReturn("pass2").whenever(userSettings).password

        // Act
        val result2 = jiraClientProvider.client()

        // Assert
        assertThat(result2).isNotNull()
        assertThat(result1).isNotEqualTo(result2)
    }

    @Test(expected = AuthException::class)
    fun invalidHost() {
        // Assemble
        doReturn("").whenever(userSettings).host
        doReturn("user").whenever(userSettings).username
        doReturn("pass").whenever(userSettings).password

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidUsername() {
        // Assemble
        doReturn("host").whenever(userSettings).host
        doReturn("").whenever(userSettings).username
        doReturn("pass").whenever(userSettings).password

        // Act
        // Assert
        jiraClientProvider.client()
    }

    @Test(expected = AuthException::class)
    fun invalidPass() {
        // Assemble
        doReturn("host").whenever(userSettings).host
        doReturn("user").whenever(userSettings).username
        doReturn("").whenever(userSettings).password

        // Act
        // Assert
        jiraClientProvider.client()
    }

}
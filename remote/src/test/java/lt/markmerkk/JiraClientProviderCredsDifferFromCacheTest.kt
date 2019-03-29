package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.entities.JiraCreds
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class JiraClientProviderCredsDifferFromCacheTest {

    val userSettings: UserSettings = mock()
    val clientProvider = JiraClientProvider(userSettings)

    @Test
    fun valid_creditsMatch() {
        // Arrange
        val oldCredits = JiraCreds(
                hostname = "valid_host",
                username = "valid_username",
                password = "valid_password"
        )
        doReturn("valid_host").whenever(userSettings).host
        doReturn("valid_username").whenever(userSettings).username
        doReturn("valid_password").whenever(userSettings).password

        // Act
        val result = clientProvider.creditsMatchCache(oldCredits)

        // Assert
        assertTrue(result)
    }

    @Test
    fun diffHost_creditsMatch() {
        // Arrange
        val oldCredits = JiraCreds(
                hostname = "diff_host",
                username = "valid_username",
                password = "valid_password"
        )
        doReturn("valid_host").whenever(userSettings).host
        doReturn("valid_username").whenever(userSettings).username
        doReturn("valid_password").whenever(userSettings).password

        // Act
        val result = clientProvider.creditsMatchCache(oldCredits)

        // Assert
        assertFalse(result)
    }

    @Test
    fun diffUsername_creditsMatch() {
        // Arrange
        val oldCredits = JiraCreds(
                hostname = "valid_host",
                username = "diff_username",
                password = "valid_password"
        )
        doReturn("valid_host").whenever(userSettings).host
        doReturn("valid_username").whenever(userSettings).username
        doReturn("valid_password").whenever(userSettings).password

        // Act
        val result = clientProvider.creditsMatchCache(oldCredits)

        // Assert
        assertFalse(result)
    }

    @Test
    fun diffPassword_creditsMatch() {
        // Arrange
        val oldCredits = JiraCreds(
                hostname = "valid_host",
                username = "valid_username",
                password = "diff_password"
        )
        doReturn("valid_host").whenever(userSettings).host
        doReturn("valid_username").whenever(userSettings).username
        doReturn("valid_password").whenever(userSettings).password

        // Act
        val result = clientProvider.creditsMatchCache(oldCredits)

        // Assert
        assertFalse(result)
    }
}
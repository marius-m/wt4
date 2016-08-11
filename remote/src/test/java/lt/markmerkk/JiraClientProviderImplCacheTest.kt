package lt.markmerkk

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-09
 */
class JiraClientProviderImplCacheTest {
    val userSettings: UserSettings = mock()
    val jiraClientProvider = JiraClientProviderImpl(userSettings)

    @Test
    fun valid_createJiraClient() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertNotNull(result)
    }

    @Test
    fun valid_multipleCall_cacheClient() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result1 = jiraClientProvider.client()
        val result2 = jiraClientProvider.client()
        val result3 = jiraClientProvider.client()

        // Assert
        assertEquals(result1, result2)
        assertEquals(result2, result3)
        assertEquals(result3, result1)
    }

    @Test
    fun multipleCall_hostChangeInMiddle_recreateClient() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result1 = jiraClientProvider.client()
        whenever(userSettings.host).thenReturn("valid_host2")
        val result2 = jiraClientProvider.client()

        // Assert
        assertNotEquals(result1, result2)
    }

    @Test(expected = IllegalStateException::class)
    fun noHost_throwError() {
        // Arrange
//        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertNull(result)
    }

    @Test(expected = IllegalStateException::class)
    fun noUsername_throwError() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
//        whenever(userSettings.username).thenReturn("valid_username")
        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertNull(result)
    }

    @Test(expected = IllegalStateException::class)
    fun noPass_throwError() {
        // Arrange
        whenever(userSettings.host).thenReturn("valid_host")
        whenever(userSettings.username).thenReturn("valid_username")
//        whenever(userSettings.password).thenReturn("valid_pass")

        // Act
        val result = jiraClientProvider.client()

        // Assert
        assertNull(result)
    }

}
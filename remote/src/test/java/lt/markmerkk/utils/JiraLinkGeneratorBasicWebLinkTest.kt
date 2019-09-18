package lt.markmerkk.utils

import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class JiraLinkGeneratorBasicWebLinkTest {

    @Mock lateinit var view: JiraLinkGenerator.View
    @Mock lateinit var accountAvailablility: AccountAvailablility
    lateinit var jiraLinkGenerator: JiraLinkGeneratorBasic

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jiraLinkGenerator = JiraLinkGeneratorBasic(
                view = view,
                accountAvailablility = accountAvailablility
        )
    }

    @Test
    fun valid() {
        // Assemble
        doReturn("host").whenever(accountAvailablility).host()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("DEV-123")

        // Assert
        assertThat(result).isEqualTo("host/browse/DEV-123")
    }

    @Test
    fun invalidCode() {
        // Assemble
        doReturn("host").whenever(accountAvailablility).host()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("invalid")

        // Assert
        assertThat(result).isEqualTo("")
    }

    @Test
    fun noHost() {
        // Assemble
        doReturn("").whenever(accountAvailablility).host()

        // Act
        val result = jiraLinkGenerator.webLinkFromInput("DEV-123")

        // Assert
        assertThat(result).isEqualTo("")
    }
}
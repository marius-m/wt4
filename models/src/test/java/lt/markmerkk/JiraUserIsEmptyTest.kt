package lt.markmerkk

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JiraUserIsEmptyTest {

    @Test
    fun valid() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "valid_name",
                displayName = "display_name",
                email = "user@mail.com",
                accountId = "account_id"
        )

        // Act
        val result = user.isEmpty()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun empty() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "",
                displayName = "",
                email = "",
                accountId = ""
        )

        // Act
        val result = user.isEmpty()

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun noName() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "",
                displayName = "display_name",
                email = "user@mail.com",
                accountId = "account_id"
        )

        // Act
        val result = user.isEmpty()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun noDisplayName() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "valid_name",
                displayName = "",
                email = "user@mail.com",
                accountId = "account_id"
        )

        // Act
        val result = user.isEmpty()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun noEmail() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "valid_name",
                displayName = "display_name",
                email = "",
                accountId = "account_id"
        )

        // Act
        val result = user.isEmpty()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun noAccountId() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "valid_name",
                displayName = "display_name",
                email = "user@mail.com",
                accountId = ""
        )

        // Act
        val result = user.isEmpty()

        // Assert
        assertThat(result).isFalse()
    }

}
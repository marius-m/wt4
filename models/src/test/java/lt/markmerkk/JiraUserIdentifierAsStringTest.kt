package lt.markmerkk

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JiraUserIdentifierAsStringTest {

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
        val result = user.identifierAsString()

        // Assert
        assertThat(result).isEqualTo("valid_name")
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
        val result = user.identifierAsString()

        // Assert
        assertThat(result).isEqualTo("account_id")
    }

    @Test
    fun noNameNoAccountId() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "",
                displayName = "display_name",
                email = "user@mail.com",
                accountId = ""
        )

        // Act
        val result = user.identifierAsString()

        // Assert
        assertThat(result).isEqualTo("user@mail.com")
    }

    @Test
    fun noNameNoAccountNoEmail() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "",
                displayName = "display_name",
                email = "",
                accountId = ""
        )

        // Act
        val result = user.identifierAsString()

        // Assert
        assertThat(result).isEqualTo("display_name")
    }

    @Test
    fun emptyUser() {
        // Assemble
        val user = Mocks.createJiraUser(
                name = "",
                displayName = "",
                email = "",
                accountId = ""
        )

        // Act
        val result = user.identifierAsString()

        // Assert
        assertThat(result).isEqualTo("")
    }
}
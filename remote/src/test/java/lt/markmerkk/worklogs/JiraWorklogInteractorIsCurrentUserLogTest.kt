package lt.markmerkk.worklogs

import lt.markmerkk.JiraMocks
import lt.markmerkk.TimeProviderTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JiraWorklogInteractorIsCurrentUserLogTest {

    private val timeProvider = TimeProviderTest()

    @Test
    fun validDisplayName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "Display Name",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider),
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "email@mail.com",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider)
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "name",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider)
        )

        assertThat(result).isTrue()
    }

    @Test
    fun invalidName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "jonas",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider)
        )

        assertThat(result).isFalse()
    }

    @Test
    fun validDisplayName_diffCases() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "DISPLAY NAME",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider)
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "EMAIL@MAIL.COM",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider)
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validName_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "NAME",
            worklog = JiraMocks.mockWorklog(timeProvider = timeProvider)
        )

        assertThat(result).isTrue()
    }

    @Test
    fun invalidUser() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "username",
            worklog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                author = JiraMocks.mockAuthor(
                    name = "diffusername",
                    email = "user@mail.com",
                    displayName = "User Namer",
                    accountId = "",
                ),
            )
        )

        assertThat(result).isFalse()
    }

    @Test
    fun emptyIdentifier() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "",
            worklog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                author = JiraMocks.mockAuthor(
                    name = "diffusername",
                    email = "user@mail.com",
                    displayName = "User Namer",
                    accountId = "",
                ),
            )
        )

        assertThat(result).isFalse()
    }

    @Test
    fun emptyUser() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
            activeIdentifier = "",
            worklog = JiraMocks.mockWorklog(
                timeProvider = timeProvider,
                author = JiraMocks.mockAuthor(
                    name = "",
                    email = "",
                    displayName = "",
                    accountId = "",
                ),
            )
        )

        assertThat(result).isFalse()
    }
}
package lt.markmerkk.worklogs

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.rcarz.jiraclient.User
import net.rcarz.jiraclient.WorkLog
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class JiraWorklogInteractorIsCurrentUserLogTest {

    @Test
    fun validDisplayName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "Display Name",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validAccountId() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "account_id",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validAccountId_upperCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "ACCOUNT_ID",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "email@mail.com",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "name",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun invalidName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "jonas",
                worklog = mockWorklog()
        )

        assertThat(result).isFalse()
    }

    @Test
    fun validDisplayName_diffCases() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "DISPLAY NAME",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "EMAIL@MAIL.COM",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validName_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "NAME",
                worklog = mockWorklog()
        )

        assertThat(result).isTrue()
    }

    @Test
    fun emptyUser() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeIdentifier = "",
                worklog = mockWorklog(
                        name = "",
                        email = "",
                        displayName = "",
                        accountId = ""
                )
        )

        assertThat(result).isFalse()
    }

    private fun mockWorklog(
            name: String = "name",
            email: String = "email@mail.com",
            displayName: String = "Display name",
            accountId: String = "account_id"
    ): WorkLog {
        val author: User = mock()
        doReturn(email).whenever(author).email
        doReturn(displayName).whenever(author).displayName
        doReturn(name).whenever(author).name
        doReturn(accountId).whenever(author).accountId
        val worklog: WorkLog = mock()
        doReturn(author).whenever(worklog).author
        return worklog
    }

}
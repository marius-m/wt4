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
                activeUsername = "mariusm",
                worklog = mockWorklog("marius@gmail.com", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "mariusm@gmail.com",
                worklog = mockWorklog("mariusm@gmail.com", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun invalidName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "jonas",
                worklog = mockWorklog("mariusm@gmail.com", "mariusm")
        )

        assertThat(result).isFalse()
    }

    @Test
    fun validDisplayName_diffCases() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "MARIUSM",
                worklog = mockWorklog("marius@gmail.com", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "MARIUSM@gmail.com",
                worklog = mockWorklog("mariusm@gmail.com", "mariusm")
        )

        assertThat(result).isTrue()
    }

    private fun mockWorklog(
            email: String,
            displayName: String
    ): WorkLog {
        val author: User = mock()
        doReturn(email).whenever(author).email
        doReturn(displayName).whenever(author).displayName
        val worklog: WorkLog = mock()
        doReturn(author).whenever(worklog).author
        return worklog
    }

}
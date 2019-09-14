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
                activeUsername = "Marius Merkevičius",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "marius@gmail.com",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "mariusm",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun invalidName() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "jonas",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isFalse()
    }

    @Test
    fun validDisplayName_diffCases() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "MARIUS MERKEVIČIUS",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validEmail_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "MARIUS@gmail.com",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isTrue()
    }

    @Test
    fun validName_diffCase() {
        val result = JiraWorklogInteractor.isCurrentUserLog(
                activeUsername = "MARIUSM",
                worklog = mockWorklog("marius@gmail.com", "Marius Merkevičius", "mariusm")
        )

        assertThat(result).isTrue()
    }

    private fun mockWorklog(
            email: String,
            displayName: String,
            name: String
    ): WorkLog {
        val author: User = mock()
        doReturn(email).whenever(author).email
        doReturn(displayName).whenever(author).displayName
        doReturn(name).whenever(author).name
        val worklog: WorkLog = mock()
        doReturn(author).whenever(worklog).author
        return worklog
    }

}
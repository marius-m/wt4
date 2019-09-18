package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.utils.AccountAvailablilityInteractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AccountAvailabilityInteractorBasicTest {

    @Mock lateinit var userSettings: UserSettings
    lateinit var accountAvailablilityInteractor: AccountAvailablilityInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        accountAvailablilityInteractor = AccountAvailabilityInteractorBasic(userSettings)
    }

    @Test
    fun valid() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host",
                username = "user",
                password = "pass"
        )).whenever(userSettings).jiraBasicCreds()

        val result = accountAvailablilityInteractor.isAccountReadyForSync()

        assertThat(result).isTrue()
    }

    @Test
    fun noHostname() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "",
                username = "user",
                password = "pass"
        )).whenever(userSettings).jiraBasicCreds()

        val result = accountAvailablilityInteractor.isAccountReadyForSync()

        assertThat(result).isFalse()
    }

    @Test
    fun noUsername() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host",
                username = "",
                password = "pass"
        )).whenever(userSettings).jiraBasicCreds()

        val result = accountAvailablilityInteractor.isAccountReadyForSync()

        assertThat(result).isFalse()
    }

    @Test
    fun noPass() {
        // Assemble
        doReturn(Mocks.createJiraBasicCreds(
                hostname = "host",
                username = "user",
                password = ""
        )).whenever(userSettings).jiraBasicCreds()

        val result = accountAvailablilityInteractor.isAccountReadyForSync()

        assertThat(result).isFalse()
    }
}
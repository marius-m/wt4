package lt.markmerkk

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import lt.markmerkk.utils.AccountAvailablility
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.TestScheduler

class AutoSyncWatcher2ApplyTimeChangeRuleTest {

    @Mock lateinit var eventBus: WTEventBus
    @Mock lateinit var accountAvailability: AccountAvailablility
    lateinit var watcher: AutoSyncWatcher2

    private val timeProvider = TimeProviderTest()
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        watcher = AutoSyncWatcher2(
                timeProvider = timeProvider,
                eventBus = eventBus,
                accountAvailablility = accountAvailability,
                ioScheduler = testScheduler,
                uiScheduler = testScheduler
        )
    }

    @Test
    fun noChange() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now()
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncNoChanges
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun shortChange_unscheduled() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusMinutes(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncNoChanges
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun longChange_unscheduled() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusHours(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncTriggerLongChange
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun tooLongOfUnscheduledSyncs() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusHours(3)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule1 = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule1 = resultSyncRule1 as AutoSyncAppInSleepMode
        assertThat(rule1).isNotNull()
    }

    @Test
    fun tooLongUnscheduled_triggerScheduledTime() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusHours(3)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now().plusHours(3)

        // Act
        val resultSyncRule1 = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule1 = resultSyncRule1 as AutoSyncTriggerShortChange
        assertThat(rule1).isNotNull()
    }

    @Test
    fun scheduleNextSync() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusMinutes(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now().plusMinutes(2)

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncTriggerShortChange
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun scheduled_longerThanPeriodicChange() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusMinutes(110)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now().plusMinutes(120)

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncNoChangesWaitingForScheduled
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun blockingUpdate() {
        // Assemble
        doReturn(true).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusHours(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        watcher.changeUpdateLock(true, "test")
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncOtherProcessInProgress
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun accountNotAvailable() {
        // Assemble
        doReturn(false).whenever(accountAvailability).isAccountReadyForSync()
        val now = timeProvider.now().plusHours(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                lastAppUsage = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncAccountNotReady
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }
}
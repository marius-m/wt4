package lt.markmerkk

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import rx.schedulers.TestScheduler

class AutoSyncWatcher2ApplyTimeChangeRuleTest {

    @Mock lateinit var eventBus: WTEventBus
    lateinit var watcher: AutoSyncWatcher2

    private val timeProvider = TimeProviderTest()
    private val testScheduler = TestScheduler()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        watcher = AutoSyncWatcher2(
                timeProvider = timeProvider,
                eventBus = eventBus,
                ioScheduler = testScheduler,
                uiScheduler = testScheduler
        )
    }

    @Test
    fun noChange() {
        // Assemble
        val now = timeProvider.now()
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
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
        val now = timeProvider.now().plusMinutes(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
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
        val now = timeProvider.now().plusHours(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncTriggerLongChange
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }

    @Test
    fun scheduleNextSync() {
        // Assemble
        val now = timeProvider.now().plusMinutes(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now().plusMinutes(2)

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
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
        val now = timeProvider.now().plusMinutes(110)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now().plusMinutes(120)

        // Act
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
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
        val now = timeProvider.now().plusHours(2)
        val lastSync = timeProvider.now()
        val nextSync = timeProvider.now()

        // Act
        watcher.changeUpdateLock(true, "test")
        val resultSyncRule = watcher.applyTimeChangeRule(
                now = now,
                lastSync = lastSync,
                nextSync = nextSync
        )

        // Assert
        val rule = resultSyncRule as AutoSyncOtherProcessInProgress
        assertThat(rule.now).isEqualTo(now)
        assertThat(rule.lastSync).isEqualTo(lastSync)
    }
}
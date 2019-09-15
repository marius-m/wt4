package lt.markmerkk

import lt.markmerkk.events.EventAutoSync
import lt.markmerkk.events.EventAutoSyncLastUpdate
import org.joda.time.DateTime
import org.joda.time.Duration
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Responsible for triggering sync
 * Lifecycle: Application scope [onAttach], [onDetach]
 */
class AutoSyncWatcher2(
        private val timeProvider: TimeProvider,
        private val eventBus: WTEventBus,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) {

    // Some other process may block auto update
    private val isInLock = AtomicBoolean()
    private var lockProcessName: String = ""
    private var lastSync: DateTime = timeProvider.now()
    private var nextSync: DateTime = timeProvider.now()

    private var subsTick: Subscription? = null

    fun onAttach() {}
    fun onDetach() {
        subsTick?.unsubscribe()
    }

    fun lastSyncDuration(): Duration = Duration(lastSync, timeProvider.now())

    /**
     * Starts synchronization watching
     */
    fun subscribeWatch() {
        lastSync = timeProvider.now()
        subsTick?.unsubscribe()
        subsTick = Observable.interval(1L, 1L, TimeUnit.MINUTES, ioScheduler)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    val now = timeProvider.now()
                    val rule = applyTimeChangeRule(
                            now = timeProvider.now(),
                            lastSync = lastSync,
                            nextSync = nextSync
                    )
                    handleRuleChanges(now, rule)
                }, {
                    logger.error("Auto update result in error", it)
                })
    }

    /**
     * Figures out action needed to be taken with time changes
     * Note: When [lastSync] >= [nextSync], short schedule does not apply
     * @param now current date time
     * @param lastSync date time of last synchronization trigger
     * @param nextSync next scheduled synchronization
     */
    fun applyTimeChangeRule(
            now: DateTime,
            lastSync: DateTime,
            nextSync: DateTime
    ): AutoSyncRule {
        val isNextSyncScheduled = nextSync.isAfter(lastSync)
        if (isInLock.get()) {
            return AutoSyncOtherProcessInProgress(now, lastSync, lockProcessName)
        }
        val currentDuration = Duration(lastSync, now)
        val scheduledSyncTriggered = now.isAfter(nextSync) || now.isEqual(nextSync)
        if (isNextSyncScheduled) {
            if (scheduledSyncTriggered) {
                return AutoSyncTriggerShortChange(now, lastSync)
            }
            return AutoSyncNoChangesWaitingForScheduled(now, lastSync, nextSync)
        } else {
            if (currentDuration.standardHours >= 1) {
                return AutoSyncTriggerLongChange(now, lastSync)
            }
            return AutoSyncNoChanges(now, lastSync)
        }
    }

    fun handleRuleChanges(
            now: DateTime,
            rule: AutoSyncRule
    ) {
        val currentDuration = Duration(lastSync, now)
        when (rule) {
            is AutoSyncNoChanges -> {
                logger.debug("Skip update as last update was in $currentDuration")
                eventBus.post(EventAutoSyncLastUpdate(currentDuration))
            }
            is AutoSyncNoChangesWaitingForScheduled -> {
                logger.debug("Skip update as last update was in $currentDuration. Waiting for scheduled time ${rule.nextSync}")
                eventBus.post(EventAutoSyncLastUpdate(currentDuration))
            }
            is AutoSyncOtherProcessInProgress -> {
                logger.debug("Skip update as some other process is blocking update: ${rule.lockProcessName}")
                eventBus.post(EventAutoSyncLastUpdate(currentDuration))
            }
            is AutoSyncTriggerLongChange,
            is AutoSyncTriggerShortChange -> {
                logger.debug("Triggering update (${rule.javaClass})")
                lastSync = now
                nextSync = now
                val newDuration = Duration(lastSync, now)
                eventBus.post(EventAutoSync())
                eventBus.post(EventAutoSyncLastUpdate(newDuration))
            }
        }.javaClass
    }

    /**
     * Marks watcher for short update cycle (ex.: 2 min instead of 1 hour)
     */
    fun markForShortCycleUpdate() {
        logger.debug("Marking auto sync for shorter update cycle")
        nextSync = timeProvider.now().plusMinutes(2)
    }

    fun changeUpdateLock(
            isInLock: Boolean,
            lockProcessName: String
    ) {
        logger.debug("Applying lock to auto sync watcher: $isInLock ($lockProcessName)")
        this.isInLock.set(isInLock)
        this.lockProcessName = lockProcessName
    }

    fun reset() {
        val now = timeProvider.now()
        logger.debug("Resetting watcher")
        lastSync = now
        nextSync = now
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AutoSyncWatcher2::class.java)!!
    }

}

sealed class AutoSyncRule
/**
 * Indicates not ready for edit
 */
data class AutoSyncNoChanges(
        val now: DateTime,
        val lastSync: DateTime
): AutoSyncRule()

/**
 * Indicates not ready for edit, waiting for scheduled change
 */
data class AutoSyncNoChangesWaitingForScheduled(
        val now: DateTime,
        val lastSync: DateTime,
        val nextSync: DateTime
): AutoSyncRule()

/**
 * Indicates that other process is currently blocking the synchronization trigger
 */
data class AutoSyncOtherProcessInProgress(
        val now: DateTime,
        val lastSync: DateTime,
        val lockProcessName: String
): AutoSyncRule()

/**
 * Indicates a notification to synchronize after recent update
 */
data class AutoSyncTriggerShortChange(
        val now: DateTime,
        val lastSync: DateTime
): AutoSyncRule()

/**
 * Indicates a notification to synchronize after a periodic update
 */
data class AutoSyncTriggerLongChange(
        val now: DateTime,
        val lastSync: DateTime
): AutoSyncRule()

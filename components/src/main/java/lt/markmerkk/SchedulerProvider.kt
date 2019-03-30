package lt.markmerkk

import rx.Scheduler

interface SchedulerProvider {
    /**
     * Scheduler that is bound for incoming data whenever screen / app is active
     */
    fun waitScheduler(): Scheduler

    /**
     * Generic new thread
     */
    fun spawnNew(): Scheduler

    /**
     * Heavy duty threads
     */
    fun io(): Scheduler

    /**
     * Ui thread
     */
    fun ui(): Scheduler
}
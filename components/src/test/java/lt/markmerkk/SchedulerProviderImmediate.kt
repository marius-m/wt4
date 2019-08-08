package lt.markmerkk

import rx.Scheduler
import rx.schedulers.Schedulers

class SchedulerProviderImmediate: SchedulerProvider {
    override fun waitScheduler(): Scheduler = Schedulers.immediate()

    override fun spawnNew(): Scheduler = Schedulers.immediate()

    override fun io(): Scheduler = Schedulers.immediate()

    override fun ui(): Scheduler = Schedulers.immediate()
}
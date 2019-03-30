package lt.markmerkk

import rx.Scheduler
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers

class SchedulerProviderFx : SchedulerProvider {

    private val uiScheduler = JavaFxScheduler.getInstance()

    override fun waitScheduler(): Scheduler = Schedulers.newThread()
    override fun spawnNew(): Scheduler = Schedulers.newThread()
    override fun io(): Scheduler = Schedulers.io()
    override fun ui(): Scheduler = uiScheduler

}
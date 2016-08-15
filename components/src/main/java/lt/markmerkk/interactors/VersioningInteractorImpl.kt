package lt.markmerkk.interactors

import lt.markmerkk.interactors.VersioningInteractor
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
class VersioningInteractorImpl(
        private val versionUpdaterInteractor: lt.markmerkk.ui.version.VersionUpdater,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : VersioningInteractor {

    var loading = false
    var subscription: Subscription? = null

    override fun onAttach() {
//        versionUpdaterInteractor.progressSubject
//                .subscribeOn(ioScheduler)
//                .observeOn(uiScheduler)
//                .doOnNext { logger.debug("Status: $it") }
//                .subscribe()
        checkVersion()
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun checkVersion() {
        if (loading) {
            logger.debug("Already checking for a new version!")
            return
        }
        subscription = Observable.just(versionUpdaterInteractor)
                .subscribeOn(ioScheduler)
                .flatMap {
                    it.run()
                    Observable.just(it)
                }
                .doOnSubscribe { loading = true }
                .doOnUnsubscribe { loading = false }
                .observeOn(uiScheduler)
                .subscribe({
                    logger.debug("Success with ${versionUpdaterInteractor.value}!")
                }, {
                    logger.debug("fail")
                })
    }

    companion object {
        val logger = LoggerFactory.getLogger(VersioningInteractorImpl::class.java)!!
    }

}
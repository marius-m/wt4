package lt.markmerkk.ui.version

import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
class VersioningInteractorImpl(
        private val updaterInteractor: UpdaterImpl,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : VersioningInteractor {

    var loading = false
    var subscription: Subscription? = null

    override fun onAttach() {
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
        subscription = Observable.defer { Observable.just(updaterInteractor.get()) }
                .doOnSubscribe { loading = true }
                .doOnUnsubscribe { loading = false }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    logger.debug("Success!")
                }, {
                    logger.debug("fail")
                })
    }

    companion object {
        val logger = LoggerFactory.getLogger(VersioningInteractorImpl::class.java)!!
    }

}
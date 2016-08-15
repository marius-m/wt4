package lt.markmerkk.interactors

import lt.markmerkk.VersionSummary
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
class VersioningInteractorImpl(
        private val versionUpdaterInteractor: VersionUpdater,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : VersioningInteractor {

    override var loading = false
        set(value) {
            field = value
            loadingListener.forEach { it.onVersionLoadChange(value) }
        }
    override var cacheUpdateSummary: VersionSummary? = null
    var subscription: Subscription? = null
    val loadingListener = mutableListOf<VersioningInteractor.LoadingListener>()

    override fun onAttach() {
        checkVersion()
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    override fun registerLoadingListener(listener: VersioningInteractor.LoadingListener) {
        loadingListener += listener
    }

    override fun unregisterLoadingListener(listener: VersioningInteractor.LoadingListener) {
        loadingListener -= listener
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
                    logger.debug("Success running updater")
                    cacheUpdateSummary = versionUpdaterInteractor.value
                }, {
                    logger.debug("Failed running updater")
                })
    }

    companion object {
        val logger = LoggerFactory.getLogger(VersioningInteractorImpl::class.java)!!
    }

}
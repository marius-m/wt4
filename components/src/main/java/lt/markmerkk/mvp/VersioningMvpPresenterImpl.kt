package lt.markmerkk.mvp

import lt.markmerkk.interactors.VersionUpdater
import lt.markmerkk.interactors.VersioningInteractor
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription

/**
 * @author mariusmerkevicius
 * @since 2016-08-15
 */
class VersioningMvpPresenterImpl(
        private val view: VersioningMvp.View,
        private val versionUpdaterInteractor: VersionUpdater,
        private val versioningInteractor: VersioningInteractor,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) : VersioningMvp.Presenter, VersioningInteractor.LoadingListener {

    var subscription: Subscription? = null

    override fun onAttach() {
        subscription = versionUpdaterInteractor.progressSubject
                .subscribe({ view.showProgress(it) })
        checkUpdateSummary()
        versioningInteractor.registerLoadingListener(this)
    }

    override fun onDetach() {
        versioningInteractor.unregisterLoadingListener(this)
        subscription?.unsubscribe()
    }

    fun checkUpdateSummary() {
        if (versioningInteractor.loading) {
            view.showUpdateInProgress()
        } else {
            if (versioningInteractor.cacheUpdateSummary != null) {
                view.showUpdateAvailable()
            } else {
                view.showUpdateUnavailable()
            }
        }
    }

    override fun onVersionLoadChange(loading: Boolean) {
        checkUpdateSummary()
    }

    companion object {
        val logger = LoggerFactory.getLogger(VersioningMvpPresenterImpl::class.java)!!
    }

}
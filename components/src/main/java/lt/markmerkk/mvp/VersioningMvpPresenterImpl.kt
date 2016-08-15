package lt.markmerkk.mvp

import lt.markmerkk.interactors.VersionUpdater
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
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
) : VersioningMvp.Presenter {

    var subscription: Subscription? = null

    override fun onAttach() {
        subscription = versionUpdaterInteractor.progressSubject
                .subscribe({
                    logger.debug("Update $it")
                    view.showProgress(it)
                })
    }

    override fun onDetach() {
        subscription?.unsubscribe()
    }

    companion object {
        val logger = LoggerFactory.getLogger(VersioningMvpPresenterImpl::class.java)!!
    }

}
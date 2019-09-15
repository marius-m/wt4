package lt.markmerkk.ui_2.views.progress

import lt.markmerkk.AutoSyncWatcher2
import lt.markmerkk.Tags
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.interfaces.IRemoteLoadListener
import lt.markmerkk.utils.LogFormatters
import org.slf4j.LoggerFactory

class ProgressWidgetPresenter(
        private val syncInteractor: SyncInteractor,
        private val autoSyncWatcher: AutoSyncWatcher2
): ProgressContract.Presenter {

    private var view: ProgressContract.View? = null

    override fun onAttach(view: ProgressContract.View) {
        this.view = view
        syncInteractor.addLoadingListener(remoteLoadListener)
        handleSyncChange(syncInteractor.isLoading())
    }

    override fun onDetach() {
        this.view = null
        syncInteractor.removeLoadingListener(remoteLoadListener)
    }

    override fun onClickSync() {
        syncInteractor.syncLogs()
        autoSyncWatcher.reset()
    }

    private fun handleSyncChange(isLoading: Boolean) {
        if (isLoading) {
            view?.showProgress()
            view?.changeLabel("Synchronizing with JIRA...")
        } else {
            view?.hideProgress()
            val prettyDuration = LogFormatters.humanReadableDuration(autoSyncWatcher.lastSyncDuration())
            view?.changeLabel("Last update: $prettyDuration")
        }
    }

    //region Listeners

    private val remoteLoadListener = object : IRemoteLoadListener {
        override fun onLoadChange(loading: Boolean) {
            handleSyncChange(loading)
        }

        override fun onError(error: String?) {
            view?.hideProgress()
            handleSyncChange(false)
        }

        override fun onAuthError() { }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.MAIN)
    }

    //endregion

}
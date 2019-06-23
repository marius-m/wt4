package lt.markmerkk.ui_2.views.progress

import lt.markmerkk.Tags
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.interfaces.IRemoteLoadListener
import org.slf4j.LoggerFactory

class ProgressWidgetPresenter(
        private val syncInteractor: SyncInteractor
): ProgressContract.Presenter {

    private var view: ProgressContract.View? = null

    override fun onAttach(view: ProgressContract.View) {
        this.view = view
        syncInteractor.addLoadingListener(remoteLoadListener)
        if (syncInteractor.isLoading()) {
            view.showProgress()
        } else {
            view.hideProgress()
        }
    }

    override fun onDetach() {
        this.view = null
        syncInteractor.removeLoadingListener(remoteLoadListener)
    }

    override fun onClickSync() {
        syncInteractor.syncLogs()
    }

    //region Listeners

    private val remoteLoadListener = object : IRemoteLoadListener {
        override fun onLoadChange(loading: Boolean) {
            if (loading) {
                view?.showProgress()
            } else {
                view?.hideProgress()
            }
        }

        override fun onError(error: String?) {
            view?.hideProgress()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Tags.MAIN)
    }

    //endregion

}
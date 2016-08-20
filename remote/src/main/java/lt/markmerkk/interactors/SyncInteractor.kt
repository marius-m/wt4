package lt.markmerkk.interactors

import lt.markmerkk.interfaces.IRemoteLoadListener

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
interface SyncInteractor {
    fun isLoading(): Boolean

    fun onAttach()
    fun onDetach()

    fun syncAll()
    fun syncLogs()
    fun syncIssues()
    fun addLoadingListener(listener: IRemoteLoadListener)
    fun removeLoadingListener(listener: IRemoteLoadListener)
}
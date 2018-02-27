package lt.markmerkk.interactors

import lt.markmerkk.interfaces.IRemoteLoadListener

interface SyncInteractor {
    fun isLoading(): Boolean

    fun onAttach()
    fun onDetach()

    fun stop()
    fun syncAll()
    fun syncLogs()
    fun syncIssues()
    fun addLoadingListener(listener: IRemoteLoadListener)
    fun removeLoadingListener(listener: IRemoteLoadListener)
}
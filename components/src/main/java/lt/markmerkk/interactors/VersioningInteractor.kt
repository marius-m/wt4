package lt.markmerkk.interactors

import lt.markmerkk.VersionSummary

/**
 * @author mariusmerkevicius
 * @since 2016-08-14
 */
interface VersioningInteractor {
    val loading: Boolean
    val cacheUpdateSummary: VersionSummary?

    fun onAttach()
    fun onDetach()

    fun registerLoadingListener(listener: LoadingListener)
    fun unregisterLoadingListener(listener: LoadingListener)

    fun checkVersion()

    interface LoadingListener {
        fun onVersionLoadChange(loading: Boolean)
    }
}
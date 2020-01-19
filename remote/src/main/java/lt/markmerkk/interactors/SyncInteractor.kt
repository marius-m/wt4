package lt.markmerkk.interactors

import lt.markmerkk.interfaces.IRemoteLoadListener
import org.joda.time.LocalDate

interface SyncInteractor {
    fun isLoading(): Boolean

    fun onAttach()
    fun onDetach()

    fun stop()
    fun syncActiveTime()
    fun syncLogs(
            startDate: LocalDate,
            endDate: LocalDate
    )
    fun addLoadingListener(listener: IRemoteLoadListener)
    fun removeLoadingListener(listener: IRemoteLoadListener)
}
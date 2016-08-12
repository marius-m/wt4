package lt.markmerkk.interactors

/**
 * @author mariusmerkevicius
 * @since 2016-08-12
 */
interface KeepAliveInteractor {
    fun register(listener: Listener)
    fun unregister(listener: Listener)

    fun onAttach()
    fun onDetach()

    interface Listener {
        fun update()
    }

}
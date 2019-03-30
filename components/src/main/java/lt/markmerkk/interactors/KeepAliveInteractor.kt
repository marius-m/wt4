package lt.markmerkk.interactors

interface KeepAliveInteractor {
    fun register(listener: Listener)
    fun unregister(listener: Listener)

    fun onAttach()
    fun onDetach()

    interface Listener {
        fun update()
    }

}
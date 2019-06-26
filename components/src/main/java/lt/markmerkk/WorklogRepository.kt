package lt.markmerkk

class WorklogRepository(
        private val timeProvider: TimeProvider,
        private val eventBus: WTEventBus
) {

    //region Listener

    private val listeners = mutableListOf<Listener>()

    fun register(listener: Listener) {
        listeners += listener
    }

    fun unregister(listener: Listener) {
        listeners -= listener
    }

    //endregion

    //region Actions



    //endregion

    /**
     * Notifies all listeners that worklogs were updated
     */
    interface Listener {
        fun onWorklogsUpdate()
    }
}
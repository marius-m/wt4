package lt.markmerkk.widgets.main

/**
 * Handles side panel states
 */
class SidePaneHandler(
        private val listener: Listener,
        private val paneLogs: SidePaneStateProvider,
        private val paneTickets: SidePaneStateProvider
) {

    fun notifyOnSidePaneChange() {
        if (paneLogs.isOpen() || paneTickets.isOpen()) {
            listener.onAnySidePaneOpen()
            return
        }
        listener.onAllPanesClosed()
    }

    interface Listener {
        fun onAnySidePaneOpen()
        fun onAllPanesClosed()
    }

    interface SidePaneStateProvider {
        fun state(): SidePaneState
        fun isOpen(): Boolean
    }

    data class SidePaneState(
            val isOpening: Boolean,
            val isOpened: Boolean,
            val isClosing: Boolean,
            val isClosed: Boolean
    )

}
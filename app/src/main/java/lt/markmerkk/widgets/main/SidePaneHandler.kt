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
        listener.onSidePanelStateChange(sidePanelState())
    }

    fun sidePanelState(): PaneState {
        if (paneLogs.isOpen() && !paneTickets.isOpen()) {
            return PaneState.OPEN_ONLY_LOGS
        }
        if (paneLogs.isOpen() || paneTickets.isOpen()) {
            return PaneState.OPEN_ALL
        }
        return PaneState.CLOSED
    }

    enum class PaneState {
        CLOSED,
        OPEN_ONLY_LOGS,
        OPEN_ALL,
        ;
    }

    interface Listener {
        fun onSidePanelStateChange(state: PaneState)
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
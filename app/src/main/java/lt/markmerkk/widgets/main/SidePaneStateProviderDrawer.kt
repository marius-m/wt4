package lt.markmerkk.widgets.main

import com.jfoenix.controls.JFXDrawer

class SidePaneStateProviderDrawer(
        private val jfxDrawer: JFXDrawer
): SidePaneHandler.SidePaneStateProvider {

    override fun isOpen(): Boolean {
        val paneState = state()
        return paneState.isOpened || paneState.isOpening
    }

    override fun state(): SidePaneHandler.SidePaneState {
        return SidePaneHandler.SidePaneState(
                isOpening = jfxDrawer.isOpening,
                isOpened = jfxDrawer.isOpened,
                isClosing = jfxDrawer.isClosing,
                isClosed = jfxDrawer.isClosed
        )
    }
}
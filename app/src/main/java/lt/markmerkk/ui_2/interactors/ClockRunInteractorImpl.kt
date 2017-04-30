package lt.markmerkk.ui_2.interactors

import lt.markmerkk.ui_2.bridges.UIEButtonClock
import lt.markmerkk.ui_2.bridges.UIECommitContainer

class ClockRunInteractorImpl(
        private val commitContainer: UIECommitContainer,
        private val clockButton: UIEButtonClock
) : ClockRunInteractor {

    override fun setRunning(isRunning: Boolean) {
        if (isRunning) {
            commitContainer.show()
            clockButton.show()
        } else {
            commitContainer.hide()
            clockButton.hide()
        }
    }

}
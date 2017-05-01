package lt.markmerkk.interactors

import lt.markmerkk.interactors.ClockRunBridge
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui.UIElementText
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass

class ClockRunBridgeImpl(
        private val commitContainer: UIElement<Any>,
        private val clockButton: UIElementText<Any>,
        private val hourGlass: HourGlass
) : ClockRunBridge {

    /**
     * Initializes state changes
     */
    override fun setRunning(isRunning: Boolean) {
        hourGlass.setListener(hourglassListener)
        if (isRunning) {
            commitContainer.show()
            clockButton.show()
            hourGlass.start()
        } else {
            commitContainer.hide()
            clockButton.hide()
            hourGlass.stop()
        }
    }

    private val hourglassListener: HourGlass.Listener = object : HourGlass.Listener {
        override fun onStart(start: Long, end: Long, duration: Long) {
            clockButton.updateText(LogUtils.formatShortDuration(duration))
        }

        override fun onStop(start: Long, end: Long, duration: Long) {
            clockButton.updateText("")
        }

        override fun onTick(start: Long, end: Long, duration: Long) {
            clockButton.updateText(LogUtils.formatShortDuration(duration))
        }

        override fun onError(error: HourGlass.Error) {
            clockButton.updateText("")
        }

    }

}
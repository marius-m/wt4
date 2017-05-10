package lt.markmerkk.interactors

import lt.markmerkk.LogStorage
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.interactors.ClockRunBridge
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui.UIElementText
import lt.markmerkk.utils.IssueSplitImpl
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime

class ClockRunBridgeImpl(
        private val containerCommit: UIElement<Any>,
        private val buttonClock: UIElementText<Any>,
        private val hourGlass: HourGlass,
        private val logStorage: LogStorage
) : ClockRunBridge {

    /**
     * Initializes state changes
     */
    override fun setRunning(isRunning: Boolean) {
        hourGlass.setListener(hourglassListener)
        if (isRunning) {
            containerCommit.show()
            buttonClock.show()
            hourGlass.start()
        } else {
            containerCommit.hide()
            buttonClock.hide()
            hourGlass.stop()
        }
    }

    override fun log(message: String) {
        try {
            if (hourGlass.state == HourGlass.State.STOPPED)
                throw IllegalArgumentException("Timer is not running!")
            if (!hourGlass.isValid)
                throw IllegalArgumentException("Timer is not valid")
            val log = SimpleLogBuilder(DateTime.now().millis)
                    .setStart(hourGlass.startMillis)
                    .setEnd(hourGlass.endMillis)
                    .setTask("") // For now we do not log details
                    .setComment(message)
                    .build()
            logStorage.insert(log)
            hourGlass.restart()
//            tracker.sendEvent(
//                    GAStatics.CATEGORY_BUTTON,
//                    GAStatics.ACTION_ENTER
//            )
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
        hourGlass.restart()
        containerCommit.reset()
    }

    private val hourglassListener: HourGlass.Listener = object : HourGlass.Listener {
        override fun onStart(start: Long, end: Long, duration: Long) {
            buttonClock.updateText(LogUtils.formatShortDuration(duration))
        }

        override fun onStop(start: Long, end: Long, duration: Long) {
            buttonClock.updateText("")
        }

        override fun onTick(start: Long, end: Long, duration: Long) {
            buttonClock.updateText(LogUtils.formatShortDuration(duration))
        }

        override fun onError(error: HourGlass.Error) {
            buttonClock.updateText("-")
        }

    }

}
package lt.markmerkk.interactors

import lt.markmerkk.LogStorage
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.ui.UIElement
import lt.markmerkk.ui.UIElementText
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime

class ClockRunBridgeImpl(
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
            buttonClock.show()
            hourGlass.start()
        } else {
            buttonClock.hide()
            hourGlass.stop()
        }
    }

    override fun log(ticket: String, message: String) {
        try {
            if (hourGlass.state == HourGlass.State.STOPPED)
                throw IllegalArgumentException("Timer is not running!")
            if (!hourGlass.isValid)
                throw IllegalArgumentException("Timer is not valid")
            val log = SimpleLogBuilder(DateTime.now().millis)
                    .setStart(hourGlass.startMillis)
                    .setEnd(hourGlass.endMillis)
                    .setTask(ticket)
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
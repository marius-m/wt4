package lt.markmerkk.widgets.clock

import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.entities.SimpleLogBuilder
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import org.joda.time.DateTime

class ClockPresenter(
        private val hourGlass: HourGlass,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider
): ClockContract.Presenter {

    private var isRunning = false
    private var view: ClockContract.View? = null

    override fun onAttach(view: ClockContract.View) {
        this.view = view
        this.hourGlass.setListener(hourglassListener)
    }

    override fun onDetach() {
        this.hourGlass.setListener(null)
        this.view = null
    }

    override fun toggleClock() {
        this.isRunning = !isRunning
        if (isRunning) {
            hourGlass.start()
        } else {
            hourGlass.stop()
        }
    }

    private val hourglassListener: HourGlass.Listener = object : HourGlass.Listener {
        override fun onStart(start: Long, end: Long, duration: Long) {
            view?.showActive(LogUtils.formatShortDuration(duration))
        }

        override fun onStop(start: Long, end: Long, duration: Long) {
            view?.showInactive()
        }

        override fun onTick(start: Long, end: Long, duration: Long) {
            view?.showActive(LogUtils.formatShortDuration(duration))
        }

        override fun onError(error: HourGlass.Error) {
            view?.showActive("-")
        }

    }

}
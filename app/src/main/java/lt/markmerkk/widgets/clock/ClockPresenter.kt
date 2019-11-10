package lt.markmerkk.widgets.clock

import lt.markmerkk.LogStorage
import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventLogDetailsInitActiveClock
import lt.markmerkk.events.EventMainToggleLogDetails
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass

class ClockPresenter(
        private val hourGlass: HourGlass,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider,
        private val eventBus: WTEventBus
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
        if (!isRunning) {
            isRunning = true
            hourGlass.start()
        } else {
            suggestSavingLog()
        }
    }

    override fun cancelClock() {
        if (this.isRunning) {
            this.isRunning = false
            hourGlass.stop()
            view?.showInactive()
        }
    }

    private fun suggestSavingLog() {
        eventBus.post(EventLogDetailsInitActiveClock())
        eventBus.post(EventMainToggleLogDetails())
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
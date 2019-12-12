package lt.markmerkk.widgets.clock

import lt.markmerkk.LogStorage
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventMainToggleLogDetails
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.widgets.edit.LogDetailsSideDrawerWidget

class ClockPresenter(
        private val hourGlass: HourGlass,
        private val logStorage: LogStorage,
        private val timeProvider: TimeProvider,
        private val eventBus: WTEventBus,
        private val resultDispatcher: ResultDispatcher
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

    override fun renderClock() {
        val duration = hourGlass.duration
        if (isRunning) {
            view?.showActive(LogUtils.formatShortDuration(duration))
        } else {
            view?.showInactive()
        }
    }

    private fun suggestSavingLog() {
        resultDispatcher.publish(LogDetailsSideDrawerWidget.RESULT_DISPATCH_KEY_ACTIVE_CLOCK, true)
        eventBus.post(EventMainToggleLogDetails())
    }

    private val hourglassListener: HourGlass.Listener = object : HourGlass.Listener {
        override fun onStart(start: Long, end: Long, duration: Long) {
            renderClock()
        }

        override fun onStop(start: Long, end: Long, duration: Long) {
            renderClock()
        }

        override fun onTick(start: Long, end: Long, duration: Long) {
            renderClock()
        }

        override fun onError(error: HourGlass.Error) {
            renderClock()
        }

    }

}
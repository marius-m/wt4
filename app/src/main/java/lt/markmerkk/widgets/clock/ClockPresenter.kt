package lt.markmerkk.widgets.clock

import lt.markmerkk.LogRepository
import lt.markmerkk.ResultDispatcher
import lt.markmerkk.TimeProvider
import lt.markmerkk.WTEventBus
import lt.markmerkk.events.EventMainOpenLogDetails
import lt.markmerkk.utils.LogUtils
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.widgets.edit.LogDetailsSideDrawerWidget

class ClockPresenter(
    private val hourGlass: HourGlass,
    private val timeProvider: TimeProvider,
    private val eventBus: WTEventBus,
    private val resultDispatcher: ResultDispatcher
) : ClockContract.Presenter {

    private var view: ClockContract.View? = null

    override fun onAttach(view: ClockContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun toggleClock() {
        if (!hourGlass.isRunning()) {
            hourGlass.start()
        } else {
            suggestSavingLog()
        }
        renderClock()
    }

    override fun cancelClock() {
        if (hourGlass.isRunning()) {
            hourGlass.stop()
        }
        renderClock()
    }

    override fun renderClock() {
        val duration = hourGlass.duration
        if (hourGlass.isRunning()) {
            view?.showActive(LogUtils.formatShortDuration(duration))
        } else {
            view?.showInactive()
        }
    }

    private fun suggestSavingLog() {
        resultDispatcher.publish(LogDetailsSideDrawerWidget.RESULT_DISPATCH_KEY_ACTIVE_CLOCK, true)
        eventBus.post(EventMainOpenLogDetails())
    }

}
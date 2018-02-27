package lt.markmerkk.ui.week2

import com.airhacks.afterburner.views.FXMLView
import lt.markmerkk.ui.day.CalendarPresenter
import lt.markmerkk.ui.interfaces.UpdateListener

class WeekView2(private val updateListener: UpdateListener) : FXMLView() {
    init {
        (presenter as CalendarPresenter).updateListener = updateListener
    }
}

package lt.markmerkk.ui.day

import com.airhacks.afterburner.views.FXMLView
import lt.markmerkk.ui.interfaces.UpdateListener

class DayView(private val updateListener: UpdateListener) : FXMLView() {
    init {
        (presenter as DayPresenter).updateListener = updateListener
    }
}

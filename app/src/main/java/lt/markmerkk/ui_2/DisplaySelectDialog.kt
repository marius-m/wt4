package lt.markmerkk.ui_2

import com.airhacks.afterburner.views.FXMLView
import lt.markmerkk.DisplayType

class DisplaySelectDialog(
        currentDisplaytype: DisplayType
) : FXMLView() {
    init {
        (presenter as DisplaySelectDialogController).currentDisplayType = currentDisplaytype
    }
}

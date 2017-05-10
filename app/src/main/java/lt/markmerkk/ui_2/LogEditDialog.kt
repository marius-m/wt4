package lt.markmerkk.ui_2

import com.airhacks.afterburner.views.FXMLView
import lt.markmerkk.entities.SimpleLog

class LogEditDialog(
        entity: SimpleLog
) : FXMLView() {
    init {
        (presenter as LogEditController).initFromView(entity)
    }
}

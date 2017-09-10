package lt.markmerkk.ui_2

import com.airhacks.afterburner.views.FXMLView
import javafx.scene.layout.StackPane

class LogStatusView(
        jfxRoot: StackPane
) : FXMLView(), LogStatusCallback {

    init {
        (presenter as LogStatusController).jfxRoot = jfxRoot
    }

    override fun showLogWithId(logId: Long?) {
        (presenter as LogStatusController).showLogWithId(logId)
    }

}

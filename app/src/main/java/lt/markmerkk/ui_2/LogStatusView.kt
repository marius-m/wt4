package lt.markmerkk.ui_2

import com.airhacks.afterburner.views.FXMLView
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import lt.markmerkk.entities.SimpleLog

class LogStatusView(
        jfxRoot: StackPane
) : FXMLView(), LogStatusCallback {

    init {
        (presenter as LogStatusController).jfxRoot = jfxRoot
    }

    override fun showLogWithId(logId: Long?) {
        (presenter as LogStatusController).showLogWithId(logId)
    }

    override fun suggestGravityByLogWeekDay(simpleLog: SimpleLog): Pos {
        return (presenter as LogStatusController).suggestGravityByLogWeekDay(simpleLog)
    }

}

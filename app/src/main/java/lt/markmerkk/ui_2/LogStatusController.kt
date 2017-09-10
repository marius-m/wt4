package lt.markmerkk.ui_2

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import lt.markmerkk.LogStorage
import lt.markmerkk.Main
import lt.markmerkk.mvp.LogStatusService
import lt.markmerkk.mvp.LogStatusServiceImpl
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import java.net.URL
import java.util.*
import javax.annotation.PreDestroy
import javax.inject.Inject

class LogStatusController : Initializable, LogStatusCallback {

    @Inject lateinit var logStorage: LogStorage

    @FXML lateinit var jfxDialog: JFXDialog
    @FXML lateinit var jfxDialogLayout: JFXDialogLayout
    lateinit var jfxRoot: StackPane

    lateinit var logStatusService: LogStatusService

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        Main.Companion.component!!.presenterComponent().inject(this)
        jfxDialog.transitionType = JFXDialog.DialogTransition.TOP
        jfxDialog.isMouseTransparent = true

        logStatusService = LogStatusServiceImpl(
                logStatusServiceListener,
                Schedulers.io(),
                JavaFxScheduler.getInstance(),
                logStorage
        )
        logStatusService.onAttach()
    }

    @PreDestroy
    fun destroy() {
        logStatusService.onDetach()
    }

    override fun showLogWithId(logId: Long?) {
        logStatusService.showWithId(logId)
    }

    //region Listeners

    val logStatusServiceListener: LogStatusService.Listener = object : LogStatusService.Listener {
        override fun show(task: String, message: String) {
            // todo : missing data pass
            StackPane.setAlignment(jfxRoot, Pos.TOP_RIGHT)
            jfxDialog.show(jfxRoot)
        }

        override fun hide() {
            if (!jfxDialog.isVisible) return
            jfxDialog.close()
        }

    }

    //endregion

}
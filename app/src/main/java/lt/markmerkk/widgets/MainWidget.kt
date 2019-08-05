package lt.markmerkk.widgets

import javafx.scene.Parent
import javafx.scene.layout.StackPane
import lt.markmerkk.Config
import lt.markmerkk.LogStorage
import lt.markmerkk.SchedulerProvider
import lt.markmerkk.UserSettings
import lt.markmerkk.interactors.AutoUpdateInteractor
import lt.markmerkk.interactors.KeepAliveGASession
import lt.markmerkk.interactors.KeepAliveInteractor
import lt.markmerkk.interactors.SyncInteractor
import lt.markmerkk.ui_2.MainView2
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.tracker.ITracker
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class MainWidget: View() {

    @Inject lateinit var settings: UserSettings
    @Inject lateinit var logStorage: LogStorage
    @Inject lateinit var keepAliveInteractor: KeepAliveInteractor
    @Inject lateinit var syncInteractor: SyncInteractor
    @Inject lateinit var autoUpdateInteractor: AutoUpdateInteractor
    @Inject lateinit var appConfig: Config
    @Inject lateinit var tracker: ITracker
    @Inject lateinit var stageProperties: StageProperties
    @Inject lateinit var schedulersProvider: SchedulerProvider

    private lateinit var keepAliveGASession: KeepAliveGASession

    override val root: Parent = stackpane {
        add(MainView2().view)
    }

    override fun onDock() {
        super.onDock()
        logger.debug("onDockMainWidget")
    }

    override fun onUndock() {
        logger.debug("onUndockMainWidget")
        super.onUndock()
    }

    fun showInfo(message: String) {
        information(
                header = "Info",
                content = message
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainWidget::class.java)!!
    }

}
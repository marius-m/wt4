package lt.markmerkk

import javafx.stage.Stage
import lt.markmerkk.dagger.components.AppComponent
import lt.markmerkk.dagger.components.DaggerAppComponent
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.interactors.*
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.widgets.MainWidget
import org.slf4j.LoggerFactory
import tornadofx.*
import javax.inject.Inject

class Main : App(MainWidget::class, Styles::class), KeepAliveInteractor.Listener {

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
    private lateinit var appComponent: AppComponent

    override fun start(stage: Stage) {
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this, StageProperties(stage)))
                .build()
        appComponent.inject(this)

        DEBUG = appConfig.debug
        Translation.getInstance() // Initializing translations on first launch
        super.start(stage)

        logger.info("Running in " + config)
        settings.onAttach()
        keepAliveInteractor.onAttach()
        keepAliveInteractor.register(this)
        syncInteractor.onAttach()

        stage.width = SCENE_WIDTH.toDouble()
        stage.height = SCENE_HEIGHT.toDouble()
        stage.minWidth = SCENE_WIDTH.toDouble()
        stage.minHeight = SCENE_HEIGHT.toDouble()
        stage.title
        tracker.sendEvent(
                GAStatics.CATEGORY_BUTTON,
                GAStatics.ACTION_START
        )
        keepAliveGASession = KeepAliveGASessionImpl(
                logStorage,
                tracker,
                schedulersProvider.waitScheduler()
        )
        keepAliveGASession.onAttach()
        stageProperties.onAttach()
    }

    override fun stop() {
        stageProperties.onDetach()
        keepAliveGASession.onDetach()
        syncInteractor.onDetach()
        keepAliveInteractor.unregister(this)
        keepAliveInteractor.onDetach()
        settings.onDetach()
        tracker.stop()
        super.stop()
    }

    fun restart() {
        find<MainWidget>().showInfo("Restart app to take effect")
    }

    override fun update() {
        if (autoUpdateInteractor.isAutoUpdateTimeoutHit(System.currentTimeMillis())) {
            syncInteractor.syncLogs()
        }
    }

    companion object {
        var DEBUG = false

        var SCENE_WIDTH = 600
        var SCENE_HEIGHT = 500

        @JvmStatic fun mainInstance(): Main = (FX.application as Main)
        @JvmStatic fun component(): AppComponent = (FX.application as Main).appComponent

        private val logger = LoggerFactory.getLogger(Main::class.java)!!
    }
}


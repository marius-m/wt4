package lt.markmerkk

import javafx.stage.Stage
import lt.markmerkk.dagger.components.AppComponent
import lt.markmerkk.dagger.components.DaggerAppComponent
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.interactors.*
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.widgets.MainWidget
import lt.markmerkk.widgets.settings.OAuthInteractor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.File
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
    @Inject lateinit var userSettings: UserSettings

    private lateinit var keepAliveGASession: KeepAliveGASession
    private lateinit var appComponent: AppComponent

    override fun start(stage: Stage) {
        setupLogger(BuildConfig.debug)
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
        userSettings.changeOAuthPreset(
                host = OAuthInteractor.URL,
                privateKey = OAuthInteractor.PRIVATE_KEY,
                consumerKey = OAuthInteractor.CONSUMER_KEY
        )
    }

    private fun setupLogger(isDebug: Boolean) {
        val configFilePath = if (isDebug) {
            "debug_log4j2.xml"
        } else {
            "prod_log4j2.xml"
        }
        val context = LogManager.getContext(false) as LoggerContext
        val file = File(javaClass.classLoader.getResource(configFilePath).file)
        context.configLocation = file.toURI()
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


package lt.markmerkk

import javafx.application.Application
import javafx.application.HostServices
import javafx.scene.Scene
import javafx.stage.Stage
import lt.markmerkk.afterburner.InjectorNoDI
import lt.markmerkk.dagger.components.AppComponent
import lt.markmerkk.dagger.components.DaggerAppComponent
import lt.markmerkk.dagger.modules.AppModule
import lt.markmerkk.interactors.*
import lt.markmerkk.ui.MainView
import lt.markmerkk.utils.tracker.ITracker
import org.apache.log4j.PatternLayout
import org.apache.log4j.Priority
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.RollingFileAppender
import org.slf4j.LoggerFactory
import rx.schedulers.Schedulers
import javax.inject.Inject

class Main : Application(), KeepAliveInteractor.Listener {
    @Inject
    lateinit var settings: UserSettings
    @Inject
    lateinit var logStorage: LogStorage
    @Inject
    lateinit var keepAliveInteractor: KeepAliveInteractor
    @Inject
    lateinit var syncInteractor: SyncInteractor
    @Inject
    lateinit var autoUpdateInteractor: AutoUpdateInteractor
    @Inject
    lateinit var config: Config
    @Inject
    lateinit var tracker: ITracker

    var keepAliveGASession: KeepAliveGASession? = null

    override fun start(stage: Stage) {
        component = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        component!!.inject(this)
        initLoggerSettings()

        DEBUG = config.debug
        Translation.getInstance() // Initializing translations on first launch
        logger.info("Running in " + config)

        settings.onAttach()
        keepAliveInteractor.onAttach()
        keepAliveInteractor.register(this)
        syncInteractor.onAttach()

        val mainView = MainView(stage)
        val scene = Scene(mainView.view)
        val cssResource1 = javaClass.getResource("/text-field-red-border.css").toExternalForm()
        scene.stylesheets.add(cssResource1)
        stage.width = SCENE_WIDTH.toDouble()
        stage.height = SCENE_HEIGHT.toDouble()
        stage.minWidth = SCENE_WIDTH.toDouble()
        stage.minHeight = SCENE_HEIGHT.toDouble()
        stage.scene = scene
        stage.show()
        stage.title = "WT4"
        tracker.sendEvent(
                GAStatics.CATEGORY_BUTTON,
                GAStatics.ACTION_START
        )
        keepAliveGASession = KeepAliveGASessionImpl(
                logStorage,
                tracker,
                Schedulers.computation())
        keepAliveGASession?.onAttach()
    }

    override fun stop() {
        keepAliveGASession?.onDetach()
        syncInteractor.onDetach()
        keepAliveInteractor.unregister(this)
        keepAliveInteractor.onDetach()
        settings.onDetach()
        tracker.stop()
        InjectorNoDI.forgetAll()
        super.stop()
    }

    override fun update() {
        if (autoUpdateInteractor.isAutoUpdateTimeoutHit(System.currentTimeMillis())) {
            syncInteractor.syncAll()
        }
    }

    //region Convenience

    private fun initLoggerSettings() {
        PropertyConfigurator.configure(javaClass.getResource("/custom_log4j.properties"))
        val fileAppenderProd = RollingFileAppender(PatternLayout(LOG_LAYOUT_PROD), config.cfgPath + "info_prod.log", true)
        fileAppenderProd.setMaxFileSize("100KB")
        fileAppenderProd.maxBackupIndex = 1
        fileAppenderProd.threshold = Priority.INFO
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderProd)

        val fileAppenderDebug = RollingFileAppender(PatternLayout(LOG_LAYOUT_DEBUG), config.cfgPath + "info.log", true)
        fileAppenderDebug.setMaxFileSize("1000KB")
        fileAppenderDebug.maxBackupIndex = 1
        fileAppenderDebug.threshold = Priority.INFO
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppenderDebug)

        val errorAppender = RollingFileAppender(PatternLayout(LOG_LAYOUT_DEBUG), config.cfgPath + "debug.log", true)
        errorAppender.setMaxFileSize("100000KB")
        errorAppender.maxBackupIndex = 1
        errorAppender.threshold = Priority.toPriority(Priority.ALL_INT)
        org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender)
    }

    //endregion

    companion object {
        val LOG_LAYOUT_DEBUG = "%d{dd-MMM-yyyy HH:mm:ss} %5p %c{1}:%L - %m%n"
        val LOG_LAYOUT_PROD = "%d{dd-MMM-yyyy HH:mm:ss} %m%n"

        var DEBUG = false

        var SCENE_WIDTH = 600
        var SCENE_HEIGHT = 500

        var component: AppComponent? = null

        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }

        private val logger = LoggerFactory.getLogger(Main::class.java)!!
    }

}
